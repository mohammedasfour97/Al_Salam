package com.alsalameg.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "record_history_table")
public class RecordHistory {

    @PrimaryKey(autoGenerate = true)
    private int id ;

    @ColumnInfo(name = "master_id")
    private String masterId;

    @ColumnInfo(name = "region")
    private String region;

    @ColumnInfo(name = "plate_num")
    private String plateNumber;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "record_name")
    private String recordName;

    @ColumnInfo(name = "record_path")
    private String recordFilePath;

    @ColumnInfo(name = "is_uploaded")
    private String isUploaded;


    public RecordHistory(int id, String masterId, String region, String plateNumber, String address, String recordName,
                         String recordFilePath, String isUploaded) {
        this.id = id;
        this.masterId = masterId;
        this.region = region;
        this.plateNumber = plateNumber;
        this.address = address;
        this.recordName = recordName;
        this.recordFilePath = recordFilePath;
        this.isUploaded = isUploaded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public String getRecordFilePath() {
        return recordFilePath;
    }

    public void setRecordFilePath(String recordFilePath) {
        this.recordFilePath = recordFilePath;
    }


    public String getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(String isUploaded) {
        this.isUploaded = isUploaded;
    }
}
