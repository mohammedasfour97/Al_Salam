package com.alsalameg.Components;

import android.app.Application;

import com.alsalameg.Api.WebService;
import com.alsalameg.Modules.ContextModule;
import com.alsalameg.TinyDB;
import com.alsalameg.UI.SplashActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component (modules = {ContextModule.class , TinyDB.class})
public interface ContextComponent {

    void inject(Application application);
}
