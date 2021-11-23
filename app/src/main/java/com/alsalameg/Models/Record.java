package com.alsalameg.Models;

public class Record {

    private String id, idRecorded, fileName, fileExtension, fileSize, heard;

    public Record(String id, String idRecorded, String fileName, String fileExtension, String fileSize, String heard) {
        this.id = id;
        this.idRecorded = idRecorded;
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.fileSize = fileSize;
        this.heard = heard;
    }

    public Record() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdRecorded() {
        return idRecorded;
    }

    public void setIdRecorded(String idRecorded) {
        this.idRecorded = idRecorded;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getHeard() {
        return heard;
    }

    public void setHeard(String heard) {
        this.heard = heard;
    }
}
