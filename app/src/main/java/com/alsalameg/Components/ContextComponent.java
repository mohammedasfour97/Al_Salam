package com.alsalameg.Components;

import com.alsalameg.Modules.ContextModule;
import com.alsalameg.UI.SplashActivity;

import dagger.Component;

@Component (modules = ContextModule.class)
public interface ContextComponent {

    void inject(SplashActivity splashActivity);
}
