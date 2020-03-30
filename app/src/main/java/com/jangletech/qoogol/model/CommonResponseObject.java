package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommonResponseObject {

    private int statusCode;
    private String message;
    private List<Exams> object;

    public List<Exams> getObject() {
        return object;
    }

    public void setObject(List<Exams> object) {
        this.object = object;
    }


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


}
