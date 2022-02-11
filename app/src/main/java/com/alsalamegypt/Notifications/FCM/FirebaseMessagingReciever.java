package com.alsalamegypt.Notifications.FCM;

import android.util.Log;

import com.alsalamegypt.Notifications.NotificationBuilder;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseMessagingReciever extends FirebaseMessagingService {
    // Override onMessageReceived() method to extract the
    // title and
    // body from the message passed in FCM
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // First case when notifications are received via
        // data event
        // Here, 'title' and 'message' are the assumed names
        // of JSON
        // attributes. Since here we do not have any data
        // payload, This section is commented out. It is
        // here only for reference purposes.
        if(remoteMessage.getData().size()>0){

            new NotificationBuilder(getApplicationContext()).buildSimpleNotification(remoteMessage.getData().get("title"),
                    remoteMessage.getData().get("message")).show();

            for (Map.Entry<String,String> entry : remoteMessage.getData().entrySet())
                Log.d("notiii", entry.getKey() + "     " + entry.getValue());
        }

        // Second case when notification payload is
        // received.
        if (remoteMessage.getNotification() != null) {
            // Since the notification is received directly from
            // FCM, the title and the body can be fetched
            // directly as below.

            new NotificationBuilder(getApplicationContext()).buildSimpleNotification(remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody()).show();
        }


    }
}
