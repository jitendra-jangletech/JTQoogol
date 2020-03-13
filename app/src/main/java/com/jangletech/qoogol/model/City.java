package com.jangletech.qoogol.model;

import java.util.List;

public class City {

    private int statusCode;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<CityObject> getObject() {
        return object;
    }

    public void setObject(List<CityObject> object) {
        this.object = object;
    }

    private String message;
    private List<CityObject> object;
}

