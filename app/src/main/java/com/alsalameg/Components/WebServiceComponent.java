package com.alsalameg.Components;

import com.alsalameg.Api.WebServices;
import com.alsalameg.BaseClasses.BaseRepository;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = WebServices.class)
public interface WebServiceComponent {

    void Inject(BaseRepository baseRepository);
}
