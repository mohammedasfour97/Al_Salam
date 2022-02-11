package com.alsalamegypt;

import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "record_history_table")
public class RecordHistory implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id ;

    @ColumnInfo(name = "master_id")
    private int masterId;

    @ColumnInfo(name = "region_id")
    private String region_id;

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


    public RecordHistory() {
    }

    public RecordHistory(int masterId, String region_id, String region, String plateNumber, String address, String recordName,
                         String recordFilePath, String isUploaded) {

        this.masterId = masterId;
        this.region = region;
        this.region_id = region_id;
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

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
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

    public String getRegion_id() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
    }
}
