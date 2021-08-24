package com.alsalameg;

import android.app.Application;


import com.alsalameg.Api.WebService;
import com.alsalameg.Components.ContextComponent;

import com.alsalameg.Components.DaggerContextComponent;
import com.alsalameg.Components.DaggerUtilsComponents;
import com.alsalameg.Components.DaggerWebServiceComponent;
import com.alsalameg.Components.WebServiceComponent;
import com.alsalameg.Modules.ContextModule;


import javax.inject.Inject;

public class MyApplication extends Application {

    @Inject
    TinyDB tinyDB;

    private static TinyDB tinyDBInstance;
    private static ContextComponent contextComponent;
    private static WebServiceComponent webServiceComponent;

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

        webServiceComponent = DaggerWebServiceComponent.builder().webService(new WebService()).build();

        new Utils(this).setLocale("ar");

        tinyDBInstance = new TinyDB(getApplicationContext());
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
}
