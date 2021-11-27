package com.alsalamegypt.Models;

public class IDName {

    private String id, name;

    public IDName(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public IDName() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
