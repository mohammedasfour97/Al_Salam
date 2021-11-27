package com.alsalamegypt.Components;

import com.alsalamegypt.Modules.NavigationControllerModule;
import com.alsalamegypt.UI.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = NavigationControllerModule.class)
public interface NavigationControllerComponent {

    void inject(MainActivity mainActivity);
}
