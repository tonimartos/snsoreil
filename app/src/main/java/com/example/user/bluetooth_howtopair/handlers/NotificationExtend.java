package com.example.user.bluetooth_howtopair.handlers;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import com.example.user.bluetooth_howtopair.R;
import com.example.user.bluetooth_howtopair.utils.Constants;


public class NotificationExtend {
    private Activity context;

    public NotificationExtend(Activity context) {
        this.context = context;
    }

    public void showNotification() {
        NotificationManager notificationManager = (NotificationManager) this.context.getSystemService("notification");
        Notification notification = new Notification(R.drawable.icon, Constants.DEVICENAME, System.currentTimeMillis());
        notification.flags |= 2;
        notification.flags |= 16;
        notification.flags |= 1;
        notification.defaults = 4;
        notification.ledARGB = -16776961;
        notification.ledOnMS = 5000;
        Intent notificationIntent = new Intent(this.context, this.context.getClass());
        notificationIntent.setAction("android.intent.action.MAIN");
        notificationIntent.addCategory("android.intent.category.LAUNCHER");
        PendingIntent contentIntent = PendingIntent.getActivity(this.context, 0, notificationIntent, 134217728);
        //notification.setLatestEventInfo(this.context, Contants.DEVICENAME, "", contentIntent);
        notificationManager.notify(0, notification);
    }

    public void cancelNotification() {
        ((NotificationManager) this.context.getSystemService("notification")).cancel(0);
    }
}

