package com.alsalameg.DI;

import com.alsalameg.DI.Modules.AppModule;
import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface UserComponent {

}
