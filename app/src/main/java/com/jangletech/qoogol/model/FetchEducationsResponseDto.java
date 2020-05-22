package com.jangletech.qoogol.model;

import java.util.List;

public class FetchEducationsResponseDto {

    private int statusCode;
    private String message;
    private List<FetchEducationResponse> object;

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

    public List<FetchEducationResponse> getObject() {
        return object;
    }

    public void setObject(List<FetchEducationResponse> object) {
        this.object = object;
    }
}
