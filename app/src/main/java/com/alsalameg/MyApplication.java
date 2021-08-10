package com.alsalameg;

import android.app.Application;


import com.alsalameg.Components.ContextComponent;
import com.alsalameg.Components.DaggerContextComponent;
import com.alsalameg.Modules.ContextModule;

public class MyApplication extends Application {

    private ContextComponent contextComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        contextComponent = DaggerContextComponent.builder().contextModule(new ContextModule(getApplicationContext())).build();
    }

    public ContextComponent getContextComponent() {
        return contextComponent;
    }
}
