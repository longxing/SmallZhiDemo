package com.iii.wifiserver;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

public class NotificationUtils {

    public NotificationUtils() {
        // TODO Auto-generated constructor stub
    }

    public static void show(Context context) {
        // TODO Auto-generated method stub
        Notification notification = new Notification(R.drawable.ic_launcher, "WifiServer", System.currentTimeMillis());

        PendingIntent p_intent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        notification.flags = Notification.FLAG_NO_CLEAR;
        notification.setLatestEventInfo(context, "WifiServer", " Don't kill me!", p_intent);
        ((Service) context).startForeground(10, notification); 
    }
}
