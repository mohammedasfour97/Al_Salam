package com.alsalameg.Components;

import com.alsalameg.Modules.NavigationControllerModule;
import com.alsalameg.UI.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = NavigationControllerModule.class)
public interface NavigationControllerComponent {

    void inject(MainActivity mainActivity);
}
