package com.example.user.myhomeautomation;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.android.service.MqttService;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import java.util.Calendar;

public class MQTTConnectionToActivity {

    AppCompatActivity CurrentActivity;
    MqttAndroidClient ClientActivity;
    String[] topicSub;
    boolean connectionGood;

    //overloading 2 constructors
    //for activity
    public MQTTConnectionToActivity(AppCompatActivity activity, String[] topics){
        CurrentActivity=activity;
        topicSub=topics;
        /////// For the MQTT connection
        String clientId = MqttClient.generateClientId();
        ClientActivity =
                new MqttAndroidClient(CurrentActivity.getApplicationContext(), Broker.broker,
                        clientId);

        try {
            IMqttToken token = ClientActivity.connect();
            token.setActionCallback(new IMqttActionListener(){
                @Override
                public void onSuccess(IMqttToken asyncActionToken){
                    // connected
                    Toast.makeText(CurrentActivity.getApplicationContext(), "Connected ", Toast.LENGTH_SHORT).show();
                    connectionGood=true;
                    if(topicSub!=null){
                        for(int i=0;i<topicSub.length;i++){
                            SubscribeToTopic(topicSub[i]);//subscribe to topic
                        }
                    }

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // connection failed
                    connectionGood=false;
                    Toast.makeText(CurrentActivity.getApplicationContext(), "Failed ! ", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }



    }//end of constructor
    //for service
    public MQTTConnectionToActivity(Context activity){
        final Context c = activity;

        /////// For the MQTT connection
        String clientId = MqttClient.generateClientId();
        ClientActivity =
                new MqttAndroidClient(c.getApplicationContext(), Broker.broker,
                        clientId);

        try {
            IMqttToken token = ClientActivity.connect();
            token.setActionCallback(new IMqttActionListener(){
                @Override
                public void onSuccess(IMqttToken asyncActionToken){
                    // connected
                    Toast.makeText(c.getApplicationContext(), "Connected ", Toast.LENGTH_SHORT).show();
                    connectionGood=true;

                    TheDBHandler dbHandler= new TheDBHandler(c,null,null,3);
                    final Scenes s = dbHandler.getAlarmDetails(getCurrentTimeString());
                    String name=s.get_sceneName();
                    String ll=s.get_lightLivingRoom();
                    String lk=s.get_lightKitchen();
                    String lo=s.get_lightOutside();
                    String lrgb=s.get_RGBlight();
                    String mg=s.get_gate();
                    String ms=s.get_shutter();

                    final NotificationCompat.Builder NotifBuilder = new NotificationCompat.Builder(c);

                    PublishToTopic("homeautomationledlight/livingroom",ll);
                    PublishToTopic("homeautomationledlight/kitchen",lk);
                    PublishToTopic("homeautomationledlight/outside",lo);
                    PublishToTopic("homeautomationledlight/rgb",lrgb);
                    PublishToTopic("homeautomationmotor/gate",mg);
                    PublishToTopic("homeautomationmotor/shutter",ms);

                    dbHandler.updateSceneInDB(
                            name,
                            ll,
                            lk,
                            lo,
                            lrgb,
                            mg,
                            ms,
                            "SET TIME",
                            "Activated"
                    );
                    dbHandler.getWritableDatabase().execSQL(
                            "UPDATE scenes SET _status='off' WHERE NOT _sceneName='"+name+"'"
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

                    NotificationManager notificationManager = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1,NotifBuilder.build());


                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // connection failed
                    connectionGood=false;

                    TheDBHandler dbHandler= new TheDBHandler(c,null,null,3);
                    final Scenes s = dbHandler.getAlarmDetails(getCurrentTimeString());

                    String name=s.get_sceneName();
                    String ll=s.get_lightLivingRoom();
                    String lk=s.get_lightKitchen();
                    String lo=s.get_lightOutside();
                    String lrgb=s.get_RGBlight();
                    String mg=s.get_gate();
                    String ms=s.get_shutter();

                    final NotificationCompat.Builder NotifBuilder = new NotificationCompat.Builder(c);

                    Toast.makeText(c.getApplicationContext(), "Failed ! ", Toast.LENGTH_SHORT).show();

                    dbHandler.updateSceneInDB(
                            name,
                            ll,
                            lk,
                            lo,
                            lrgb,
                            mg,
                            ms,
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

                    NotificationManager notificationManager = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1,NotifBuilder.build());
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }



    }//end of constructor

    public void PublishToTopic(String topic,String STR_message){

        try {
            MqttMessage message = new MqttMessage(STR_message.getBytes());
            message.setQos(0);
            ClientActivity.publish(topic, message);
            System.out.println("Message published");

        }
        catch(MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }

    }

    public void SubscribeToTopic(String topic){
        try{
            ClientActivity.subscribe(topic,0);
        }
        catch (MqttSecurityException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public MqttAndroidClient getClient(){
        return this.ClientActivity;
    }

    public boolean isConnectionGood(){
        return connectionGood;
    }

    public String getCurrentTimeString(){
        Calendar rightNow = Calendar.getInstance();
        int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
        int currentMinute = rightNow.get(Calendar.MINUTE);
        String currentTime=currentHour+":"+currentMinute;
        return  currentTime;
    }
}
