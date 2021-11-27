package com.alsalamegypt.RoomDB;
import com.alsalamegypt.RecordHistory;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface RecordHistoryDao {

    @Query("SELECT * FROM record_history_table")
    LiveData<List<RecordHistory>> getAllRecordHistory();

    @Insert
    long insert(RecordHistory recordHistory);

    @Update
    int update (RecordHistory recordHistory);

    @Query("SELECT * FROM record_history_table WHERE id > :id")
    LiveData<RecordHistory> getRecordHistory(int id);


    @Query("SELECT * FROM record_history_table WHERE record_name > :recName")
    LiveData<RecordHistory> getRecordHistory(String  recName);

    @Delete
    int delete(RecordHistory recordHistory);

    @Query("DELETE FROM record_history_table")
    int deleteAllRecordHistory();
}
