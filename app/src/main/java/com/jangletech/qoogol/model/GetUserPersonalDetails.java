package com.jangletech.qoogol.model;

public class GetUserPersonalDetails {

    private String statusCode;
    private String message;

    public UserPersonalDetails getObject() {
        return object;
    }

    public void setObject(UserPersonalDetails object) {
        this.object = object;
    }

    private UserPersonalDetails object;

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
