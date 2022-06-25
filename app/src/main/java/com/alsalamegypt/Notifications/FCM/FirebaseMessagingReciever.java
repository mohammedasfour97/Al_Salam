package com.alsalamegypt.Notifications.FCM;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.alsalamegypt.Constants;
import com.alsalamegypt.MyApplication;
import com.alsalamegypt.Notifications.NotificationBuilder;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseMessagingReciever extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if(remoteMessage.getData().size()>0){

            if (getUserTypeNum() == Integer.parseInt(remoteMessage.getData().get("Kind")))
                new NotificationBuilder(getApplicationContext()).buildSimpleNotification(remoteMessage.getData().get("title"),
                    remoteMessage.getData().get("message")).show();

            for (Map.Entry<String,String> entry : remoteMessage.getData().entrySet())
                Log.d("notiii", entry.getKey() + "     " + entry.getValue());
        }


        if (remoteMessage.getNotification() != null) {

            new NotificationBuilder(getApplicationContext()).buildSimpleNotification(remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody()).show();
        }

    }

    private int getUserTypeNum(){

        int r = 0;

        String userType = MyApplication.getTinyDB().getString(Constants.KEY_USER_TYPE);

        if(userType != null && !TextUtils.isEmpty(userType)){

            switch(userType){

                case "Listener" :
                    r = 2;
                    break;

                case "Observed" :
                    r = 1;
                    break;
            }
        }

        return r;
    }
}
