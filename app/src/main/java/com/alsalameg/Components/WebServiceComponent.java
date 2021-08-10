package com.alsalameg.Components;

import com.alsalameg.Api.WebService;
import com.alsalameg.BaseClasses.BaseRepository;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = WebService.class)
public interface WebServiceComponent {

    void Inject(BaseRepository baseRepository);
}
