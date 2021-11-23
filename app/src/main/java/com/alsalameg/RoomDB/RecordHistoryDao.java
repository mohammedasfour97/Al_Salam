package com.alsalameg.RoomDB;
import com.alsalameg.Models.RecordHistory;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface RecordHistoryDao {

    @Query("SELECT * FROM record_history_table")
    LiveData<List<RecordHistory>> getAllUsers();

    @Insert
    void insert(RecordHistory recordHistory);

    @Delete
    void delete(RecordHistory recordHistory);

    @Query("DELETE FROM record_history_table")
    void deleteAllRecordHistory();
}
