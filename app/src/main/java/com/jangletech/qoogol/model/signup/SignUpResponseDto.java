package com.jangletech.qoogol.model.signup;

public class SignUpResponseDto {

    private int statusCode;
    private String message;
    private SignUpObject object;

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

    public SignUpObject getObject() {
        return object;
    }

    public void setObject(SignUpObject object) {
        this.object = object;
    }


}
