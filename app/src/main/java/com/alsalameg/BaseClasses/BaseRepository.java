package com.alsalameg.BaseClasses;

import com.alsalameg.Api.WebService;
import com.alsalameg.MyApplication;

import javax.inject.Inject;

public class BaseRepository {

    @Inject
    public WebService webService;

    public BaseRepository(){

        MyApplication.getWebServiceComponent().Inject(this);
    }


}
