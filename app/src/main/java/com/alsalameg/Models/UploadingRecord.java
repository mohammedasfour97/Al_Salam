package com.alsalameg.Models;

public class UploadingRecord {

    private String recordName, recordFilePath;
    private Boolean isUploaded, isUploading;

    public UploadingRecord() {
    }

    public UploadingRecord(String recordFilePath, String recordName) {
        this.recordName = recordName;
        this.recordFilePath = recordFilePath;
        isUploaded = false;
        isUploading = false;
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
}
