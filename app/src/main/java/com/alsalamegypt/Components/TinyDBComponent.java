package com.alsalamegypt.Components;

import com.alsalamegypt.BaseClasses.BaseFragment;
import com.alsalamegypt.TinyDB;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = TinyDB.class)
public interface TinyDBComponent {

    void inject(BaseFragment baseFragment);
}

