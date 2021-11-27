package com.alsalamegypt.Components;

import android.app.Application;

import com.alsalamegypt.Utils;

import dagger.Component;

@Component(modules = Utils.class)
public interface UtilsComponents {

    void inject(Application application);
}
