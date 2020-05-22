package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;

public class UserProfileResponse {

    @SerializedName("Response")
    private String responseCode;

    @SerializedName("Message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }
}
