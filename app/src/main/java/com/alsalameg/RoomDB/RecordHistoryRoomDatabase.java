package com.alsalameg.RoomDB;

import com.alsalameg.Models.RecordHistory;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {RecordHistory.class}, version = 1)
public abstract class RecordHistoryRoomDatabase extends RoomDatabase {

    public abstract RecordHistoryDao RecordHistoryDao();
}
