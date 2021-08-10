package com.alsalameg.Modules;

import android.app.Activity;
import android.content.Context;

import com.alsalameg.R;
import com.alsalameg.UI.MainActivity;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
