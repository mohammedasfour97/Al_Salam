package com.alsalamegypt.Notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;

import com.alsalamegypt.R;
import com.alsalamegypt.UI.SplashActivity;
import com.alsalamegypt.Utils;

import androidx.core.app.NotificationCompat;

import com.alsalamegypt.Constants;

public class NotificationBuilder {

    private Context context;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder mBuilder;

    private int notificationId;

    private String uploadingFlName;

    public NotificationBuilder(Context context) {

        this.context = context;

        notificationManager = NotificationManagerSingleton.getInstance(context).getNotificationManager();

        notificationId = Utils.getIntRandomCode();

    }

    public NotificationBuilder buildSimpleNotification(String title, String body){

        mBuilder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body));

        /*TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        Intent intent = new Intent(context, SplashActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        mBuilder.setContentIntent(resultPendingIntent);

         */

        Intent notificationIntent = new Intent(context, SplashActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        mBuilder.setContentIntent(pendingIntent);

        return this;
    }

    public NotificationBuilder buildSimpleNotification(String title, String body, Intent intent){

        mBuilder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body));;

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        mBuilder.setContentIntent(resultPendingIntent);


        return this;
    }


    public NotificationBuilder buildProgressNotification(String uploadingFlName){

        this.uploadingFlName = uploadingFlName;

        mBuilder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(context.getResources().getString(R.string.loading) + "0%")
                .setContentText(context.getResources().getString(R.string.loading) + " " + uploadingFlName);

        mBuilder.setProgress(100, 0, false);

        return this;
    }

    public void show() {

        notificationManager.notify(notificationId, mBuilder.build());
    }

    public void showProgress(double progress){

        int prog = 0;

        if (progress == 100)
            mBuilder.setContentTitle(context.getResources().getString(R.string.succ))
                    .setContentText(context.getResources().getString(R.string.succ_upload_record) + "  (" + uploadingFlName + ")");

        else if (progress == -1)
            mBuilder.setContentTitle(context.getResources().getString(R.string.fail_delete_rec))
                    .setContentText(context.getResources().getString(R.string.error_upload_record) + "  (" + uploadingFlName + ")");
        else{

            mBuilder.setContentTitle(context.getResources().getString(R.string.loading) + progress + " %");
            prog = (int)(progress);
        }




        mBuilder.setProgress(0,(int)(prog),false);

        notificationManager.notify(notificationId, mBuilder.build());
    }


    public void hideNotification(){

        notificationManager.cancel(notificationId);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public NotificationCompat.Builder getmBuilder() {
        return mBuilder;
    }

    public void setmBuilder(NotificationCompat.Builder mBuilder) {
        this.mBuilder = mBuilder;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }
}
