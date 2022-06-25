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
    public static String KEY_USER_FULLNAME = "user_fname";

    public static String KEY_UID = "uid";

    public static int PAGE_SIZE = 10;

    public static String KEY_REC_TIME = "rec_time";

    //// Listener token for recorder to send notification for him /////
    public static String LISTENER_TOKEN;

    /// Vars for notifications ////

    public static String CHANNEL_ID = Utils.getStringRandomCode();
    public static String CHANNEL_NAME =  CHANNEL_ID + "_channel";
    public static int IMPORTANCE = NotificationManager.IMPORTANCE_LOW;

    public static AudioAttributes AUDIO_ATTRIBUTES ;

    public static NotificationChannel mChannel() {

        NotificationChannel c = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){

            AUDIO_ATTRIBUTES = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            c =  new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, Constants.IMPORTANCE);

            c.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), AUDIO_ATTRIBUTES);
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
