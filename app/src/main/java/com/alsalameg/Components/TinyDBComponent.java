package com.alsalameg.Components;

import com.alsalameg.BaseClasses.BaseFragment;
import com.alsalameg.TinyDB;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = TinyDB.class)
public interface TinyDBComponent {

    void inject(BaseFragment baseFragment);
}

