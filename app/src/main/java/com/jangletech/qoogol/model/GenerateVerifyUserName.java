package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

public class GenerateVerifyUserName {

    private String Response;

    private String Message;

    @SerializedName(Constant.userName)
    private String userNames;

    public String getResponse() {
        return Response;
    }

    public void setResponse(String response) {
        Response = response;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getUserNames() {
        return userNames;
    }

    public void setUserNames(String userNames) {
        this.userNames = userNames;
    }
}
