package com.alsalamegypt;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Constants {

    public static String NAMESPACE = "http://tempuri.org/";
    public static String URL = "http://services.alsalamegypt.com/Service.asmx";///MyService
    public static String SOAP_ACTION = "http://tempuri.org/";
    public static String ImageURl = "http://services.alsalamegypt.com/";

    public static String KEY_USERID = "user_id";
    public static String KEY_USERNAME = "username";
    public static String KEY_USER_PASSWORD = "user_password";
    public static String KEY_USER_TYPE = "user_type";

    public static String KEY_UID = "uid";

    public static int PAGE_SIZE = 10;

    /// Vars for notifications ////

    public static String channelId = Utils.getStringRandomCode();
    public static String channelName =  channelId + "_channel";
    public static int importance = NotificationManager.IMPORTANCE_HIGH;

    public static AudioAttributes audioAttributes ;

    public static NotificationChannel mChannel() {

        NotificationChannel c = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){

            audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            c =  new NotificationChannel(Constants.channelId, Constants.channelName, Constants.importance);

            c.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes);
        }

        return c;

    }


    public static boolean checkLocationPermission(Activity activity) {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED)
            return true;

        else return false;


    }

    public static boolean checkRecorderPermission(Activity activity) {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return true;


        else return false;


    }

    public static boolean checkReadAndWritePermissions(Activity activity) {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return true;

        else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            return false;

        else return false;


    }


    public static boolean checkIdentifierPermission(Activity activity) {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
            return true;

        else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE))
            return false;

        else return false;


    }
}
