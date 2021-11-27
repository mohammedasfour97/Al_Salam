package com.alsalamegypt.Modules;

import com.alsalamegypt.R;
import com.alsalamegypt.UI.MainActivity;

import javax.inject.Singleton;

import androidx.navigation.fragment.NavHostFragment;
import dagger.Module;
import dagger.Provides;

@Module
public class NavigationControllerModule {

        private MainActivity activity;

    public NavigationControllerModule(MainActivity activity){

        this.activity = activity;
    }

    @Singleton
    @Provides
    NavHostFragment navHostFragment(){

        return  (NavHostFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.my_nav_host_fragment);

    }
}
