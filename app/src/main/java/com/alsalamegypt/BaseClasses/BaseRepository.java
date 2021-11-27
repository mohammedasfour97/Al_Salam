package com.alsalamegypt.BaseClasses;

import com.alsalamegypt.Api.WebServices;
import com.alsalamegypt.MyApplication;
import com.alsalamegypt.RoomDB.RecordHistoryRoomDatabase;

import javax.inject.Inject;

public class BaseRepository {

    @Inject
    public WebServices webServices;

    @Inject
    public RecordHistoryRoomDatabase recordHistoryRoomDatabase;

    public BaseRepository(){

        MyApplication.getWebServiceComponent().Inject(this);
    }


}
