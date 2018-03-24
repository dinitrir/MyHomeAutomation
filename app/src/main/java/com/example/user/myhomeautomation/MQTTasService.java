package com.example.user.myhomeautomation;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import java.util.Calendar;

/**
 * Created by User on 3/23/2018.
 */

public class MQTTasService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this,MQTTasService.class));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Calendar rightNow = Calendar.getInstance();
        int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
        int currentMinute = rightNow.get(Calendar.MINUTE);
        String currentTime=currentHour+":"+currentMinute;

        TheDBHandler dbHandler= new TheDBHandler(getApplicationContext(),null,null,3);
        final Scenes s = dbHandler.getAlarmDetails(currentTime);

        final NotificationCompat.Builder NotifBuilder = new NotificationCompat.Builder(getApplicationContext());

        MQTTConnectionToActivity MQTTservice = new MQTTConnectionToActivity(this);


        if(MQTTservice.isConnectionGood()==true){
            //publish
            MQTTservice.PublishToTopic("homeautomationledlight/livingroom",s.get_lightLivingRoom());
            MQTTservice.PublishToTopic("homeautomationledlight/kitchen",s.get_lightKitchen());
            MQTTservice.PublishToTopic("homeautomationledlight/outside",s.get_lightOutside());
            MQTTservice.PublishToTopic("homeautomationledlight/rgb",s.get_RGBlight());
            MQTTservice.PublishToTopic("homeautomationmotor/gate",s.get_gate());
            MQTTservice.PublishToTopic("homeautomationmotor/shutter",s.get_shutter());

            dbHandler.updateSceneInDB(
                    s.get_sceneName(),
                    s.get_lightLivingRoom(),
                    s.get_lightKitchen(),
                    s.get_lightOutside(),
                    s.get_RGBlight(),
                    s.get_gate(),
                    s.get_shutter(),
                    "SET TIME",
                    "Activated"
            );
            dbHandler.getWritableDatabase().execSQL(
                    "UPDATE scenes SET _status='off' WHERE NOT _sceneName='"+s.get_sceneName()+"'"
            );
            // push notif
            NotifBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Mode Activation")
                    .setContentText(s.get_sceneName()+" activated")
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                    .setContentInfo("Info");

            NotificationManager notificationManager = (NotificationManager)this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1,NotifBuilder.build());

        }else {
            //notif part
            dbHandler.updateSceneInDB(
                    s.get_sceneName(),
                    s.get_lightLivingRoom(),
                    s.get_lightKitchen(),
                    s.get_lightOutside(),
                    s.get_RGBlight(),
                    s.get_gate(),
                    s.get_shutter(),
                    "SET TIME",
                    "off"
            );

            NotifBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Mode Activation Failed")
                    .setContentText(s.get_sceneName()+" activation failed. Check Internet connection.")
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                    .setContentInfo("Info");

            NotificationManager notificationManager = (NotificationManager)this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1,NotifBuilder.build());

        }


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
