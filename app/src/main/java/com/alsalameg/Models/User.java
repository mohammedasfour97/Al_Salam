package com.alsalameg.Models;

public class User {

    private String id, code, fullName, username, password, type, address, phone, email, note, img, idUser, idListen, dateEdit,
            modificationDate;
    private boolean error;

    public User(String id, String code, String fullName, String username, String password, String type, String address, String phone,
                String email, String note, String img, String idUser, String dateEdit, String modificationDate, boolean error) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.type = type;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.note = note;
        this.img = img;
        this.idUser = idUser;
        this.dateEdit = dateEdit;
        this.modificationDate = modificationDate;
        this.error = error;
    }

    public User(String id, String code, String fullName, String username, String password, String type, String address, String phone,
                String email, String note, String img, String idUser, String idListen, String dateEdit, String modificationDate,
                boolean error) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.type = type;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.note = note;
        this.img = img;
        this.idUser = idUser;
        this.idListen = idListen;
        this.dateEdit = dateEdit;
        this.modificationDate = modificationDate;
        this.error = error;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getDateEdit() {
        return dateEdit;
    }

    public void setDateEdit(String dateEdit) {
        this.dateEdit = dateEdit;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(String modificationDate) {
        this.modificationDate = modificationDate;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

}
