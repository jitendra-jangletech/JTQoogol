package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

public class RegisterLoginModel {

    @SerializedName("Message")
    private String message;

    public String getMessage() {
        return message != null ? message : "";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SerializedName("newOTP")
    private String newOTP;

    @SerializedName(Constant.w_user_name)
    private String w_user_name;

    @SerializedName(Constant.u_user_id)
    private String u_user_id;

    @SerializedName("Response")
    private String Response;

    public String getNewOTP() {
        return newOTP!=null?newOTP:"";
    }

    public void setNewOTP(String newOTP) {
        this.newOTP = newOTP;
    }

    public String getW_user_name() {
        return w_user_name;
    }

    public void setW_user_name(String w_user_name) {
        this.w_user_name = w_user_name;
    }

    public String getU_user_id() {
        return u_user_id!=null?u_user_id:"";
    }

    public void setU_user_id(String u_user_id) {
        this.u_user_id = u_user_id;
    }

    public String getResponse() {
        return Response!=null?Response:"";
    }

    public void setResponse(String response) {
        Response = response;
    }

}
