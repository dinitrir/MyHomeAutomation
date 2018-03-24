package com.example.user.myhomeautomation;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import java.util.Calendar;

public class AlarmNotificationForModes extends BroadcastReceiver {
    String[] topicSub;
    @Override
    public void onReceive(final Context context, Intent intent) {
       MQTTConnectionToActivity activeService = new MQTTConnectionToActivity(context);

    }


}
