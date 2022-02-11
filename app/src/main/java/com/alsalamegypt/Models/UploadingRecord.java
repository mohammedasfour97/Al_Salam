package com.alsalamegypt.Models;

import android.net.Uri;

public class UploadingRecord {

    private String recordName;
    private Uri recordFileUri;
    private Boolean isUploaded, isUploading;
    private Master master;

    public UploadingRecord() {
    }

    public UploadingRecord(Uri recordFileUri, String recordName, Master master) {
        this.recordName = recordName;
        this.recordFileUri = recordFileUri;
        this.master = master;
        isUploaded = false;
        isUploading = false;
    }


    public String getRecordName() {
        return recordName;
    }


    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }


    public Uri getRecordFileUri() {
        return recordFileUri;
    }


    public void setRecordFileUri(Uri recordFilePath) {
        this.recordFileUri = recordFilePath;
    }


    public Boolean getUploaded() {
        return isUploaded;
    }


    public void setUploaded(Boolean uploaded) {
        isUploaded = uploaded;
    }


    public Boolean getUploading() {
        return isUploading;
    }


    public void setUploading(Boolean uploading) {
        isUploading = uploading;
    }


    public Master getMaster() {
        return master;
    }


    public void setMaster(Master master) {
        this.master = master;
    }
}
