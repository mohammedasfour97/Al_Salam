package com.alsalameg.Components;

import android.app.Application;

import com.alsalameg.BaseClasses.BaseFragment;
import com.alsalameg.Utils;

import dagger.Component;

@Component(modules = Utils.class)
public interface UtilsComponents {

    void inject(Application application);
}
