package com.alsalameg.BaseClasses;

import com.alsalameg.Api.WebServices;
import com.alsalameg.MyApplication;

import javax.inject.Inject;

public class BaseRepository {

    @Inject
    public WebServices webServices;

    public BaseRepository(){

        MyApplication.getWebServiceComponent().Inject(this);
    }


}
