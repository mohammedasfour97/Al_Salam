package com.alsalamegypt.RoomDB;

import com.alsalamegypt.RecordHistory;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {RecordHistory.class}, version = 3)
public abstract class RecordHistoryRoomDatabase extends RoomDatabase {

    public abstract RecordHistoryDao RecordHistoryDao();
}
