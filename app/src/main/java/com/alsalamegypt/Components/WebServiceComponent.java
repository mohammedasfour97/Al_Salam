package com.alsalamegypt.Components;

import com.alsalamegypt.Api.WebServices;
import com.alsalamegypt.BaseClasses.BaseRepository;
import com.alsalamegypt.RoomDB.RoomDatabaseModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {WebServices.class, RoomDatabaseModule.class})
public interface WebServiceComponent {

    void Inject(BaseRepository baseRepository);
}
