package com.alsalameg;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;


import com.alsalameg.Api.WebServices;
import com.alsalameg.Components.ContextComponent;

import com.alsalameg.Components.DaggerContextComponent;
import com.alsalameg.Components.DaggerWebServiceComponent;
import com.alsalameg.Components.WebServiceComponent;
import com.alsalameg.Modules.ContextModule;
import com.google.android.material.snackbar.Snackbar;


import javax.inject.Inject;

public class MyApplication extends Application {

    @Inject
    TinyDB tinyDB;

    private static TinyDB tinyDBInstance;
    private static ContextComponent contextComponent;
    private static WebServiceComponent webServiceComponent;
    private static Context getContext;

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
        contextComponent = DaggerContextComponent.builder().contextModule(new ContextModule(getApplicationContext()))
                .tinyDB(new TinyDB(getApplicationContext())).build();

        contextComponent.inject(this);

        getContext = context;

        webServiceComponent = DaggerWebServiceComponent.builder().webServices(new WebServices()).build();

        new Utils(this).setLocale("ar");

        tinyDBInstance = new TinyDB(getApplicationContext());

        if (TextUtils.isEmpty(MyApplication.getTinyDB().getString(Constants.KEY_UID)))
            MyApplication.getTinyDB().putString("uid", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
    }

    public static ContextComponent getContextComponent() {
        return contextComponent;
    }

    public static WebServiceComponent getWebServiceComponent() {
        return webServiceComponent;
    }

    public static TinyDB getTinyDB() {
        return tinyDBInstance;
    }

    public static boolean hasInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        } else {

            Toast.makeText(getContext, getContext.getResources().getString(R.string.check_int_con), Toast.LENGTH_SHORT).show();

            return false;
        }
    }
}
