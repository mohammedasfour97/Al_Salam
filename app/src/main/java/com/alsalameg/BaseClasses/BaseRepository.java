package com.alsalameg.BaseClasses;

import com.alsalameg.Api.WebService;
import com.alsalameg.Components.DaggerWebServiceComponent;

import javax.inject.Inject;

public class BaseRepository {

    @Inject
    public WebService webService;

    public BaseRepository(){

        DaggerWebServiceComponent.builder().webService(new WebService()).build().Inject(this);
    }


}
