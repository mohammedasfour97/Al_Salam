package com.alsalamegypt.Notifications;

import android.app.NotificationManager;
import android.content.Context;

import com.alsalamegypt.Constants;

public class NotificationManagerSingleton {

    public static NotificationManagerSingleton instance;
    private NotificationManager notificationManager;
    private Context context;

    private NotificationManagerSingleton(Context context) {
        this.context = context;

        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            notificationManager.createNotificationChannel(Constants.mChannel());
        }
    }

    public static NotificationManagerSingleton getInstance(Context context) {

        if (instance == null)
            instance = new NotificationManagerSingleton(context);

        return instance;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }
}
