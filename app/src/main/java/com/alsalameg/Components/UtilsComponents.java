package com.alsalameg.Components;

import com.alsalameg.BaseClasses.BaseFragment;
import com.alsalameg.Utils;

import dagger.Component;

@Component(modules = Utils.class)
public interface UtilsComponents {

    void inject(BaseFragment baseFragment);
}
