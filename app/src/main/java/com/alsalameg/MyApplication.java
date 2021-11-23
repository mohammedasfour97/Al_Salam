package com.alsalameg;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;


import com.alsalameg.Api.WebServices;

import com.alsalameg.Components.DaggerWebServiceComponent;
import com.alsalameg.Components.WebServiceComponent;


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


        webServiceComponent = DaggerWebServiceComponent.builder().webServices(new WebServices()).build();

        new Utils(this).setLocale("ar");

        tinyDBInstance = new TinyDB(getApplicationContext());

        if (TextUtils.isEmpty(MyApplication.getTinyDB().getString(Constants.KEY_UID)))
            MyApplication.getTinyDB().putString("uid", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
    }

    public static WebServiceComponent getWebServiceComponent() {
        return webServiceComponent;
    }

    public static TinyDB getTinyDB() {
        return tinyDBInstance;
    }

}
