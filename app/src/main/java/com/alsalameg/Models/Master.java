package com.alsalameg.Models;

public class Master {

    private String id, vehicleNumber, vehicleType, location, district, longitude, latitude, status, sorting, notes, idRegions, regions, idUSER,
            dateEdit, dateCreate, type;

    public Master(String id, String vehicleNumber, String vehicleType, String location, String district, String longitude, String latitude,
                  String status, String sorting, String notes, String idRegions, String regions, String idUSER, String dateEdit,
                  String dateCreate, String type) {
        this.id = id;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.location = location;
        this.district = district;
        this.longitude = longitude;
        this.latitude = latitude;
        this.status = status;
        this.sorting = sorting;
        this.notes = notes;
        this.idRegions = idRegions;
        this.regions = regions;
        this.idUSER = idUSER;
        this.dateEdit = dateEdit;
        this.dateCreate = dateCreate;
        this.type = type;
    }

    public Master() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSorting() {
        return sorting;
    }

    public void setSorting(String sorting) {
        this.sorting = sorting;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getIdRegions() {
        return idRegions;
    }

    public void setIdRegions(String idRegions) {
        this.idRegions = idRegions;
    }

    public String getIdUSER() {
        return idUSER;
    }

    public void setIdUSER(String idUSER) {
        this.idUSER = idUSER;
    }

    public String getDateEdit() {
        return dateEdit;
    }

    public void setDateEdit(String dateEdit) {
        this.dateEdit = dateEdit;
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegions() {
        return regions;
    }

    public void setRegions(String regions) {
        this.regions = regions;
    }
}
