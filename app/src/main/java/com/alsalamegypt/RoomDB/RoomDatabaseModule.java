package com.alsalamegypt.RoomDB;

import android.content.Context;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;

@Module
public class RoomDatabaseModule {

    private Context context;

    public RoomDatabaseModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public synchronized RecordHistoryRoomDatabase getInstance() {

        return Room.databaseBuilder(context,
                    RecordHistoryRoomDatabase.class, "record_history")
                    .fallbackToDestructiveMigration()
                    .build();
        }

    }