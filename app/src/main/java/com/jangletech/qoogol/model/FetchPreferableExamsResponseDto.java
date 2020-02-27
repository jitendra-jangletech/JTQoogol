package com.jangletech.qoogol.model;

import java.util.List;

public class FetchPreferableExamsResponseDto {

    private String statusCode;
    private String message;
    private List<Exams> object;

    public List<Exams> getObject() {
        return object;
    }

    public void setObject(List<Exams> object) {
        this.object = object;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
