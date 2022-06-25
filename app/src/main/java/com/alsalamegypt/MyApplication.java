package com.alsalamegypt;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;


import com.alsalamegypt.Api.WebServices;

import com.alsalamegypt.Components.DaggerWebServiceComponent;
import com.alsalamegypt.Components.WebServiceComponent;
import com.alsalamegypt.RoomDB.RoomDatabaseModule;


import javax.inject.Inject;

public class MyApplication extends Application {

    @Inject
    TinyDB tinyDB;

    private static TinyDB tinyDBInstance;
    private static WebServiceComponent webServiceComponent;

    @Inject
    public Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        /*CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Cairo-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        */


        webServiceComponent = DaggerWebServiceComponent.builder().webServices(new WebServices())
                .roomDatabaseModule(new RoomDatabaseModule(getApplicationContext())).build();

        //recordHistoryRoomDBComponent =

        new Utils(this).setLocale("ar");

        tinyDBInstance = new TinyDB(getApplicationContext());

        if (TextUtils.isEmpty(getTinyDB().getString(Constants.KEY_UID)))
            getTinyDB().putString(Constants.KEY_UID, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

    }

    public static WebServiceComponent getWebServiceComponent() {
        return webServiceComponent;
    }

    public static TinyDB getTinyDB() {
        return tinyDBInstance;
    }

}