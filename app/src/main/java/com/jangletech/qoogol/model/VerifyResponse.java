package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

public class VerifyResponse {

    @SerializedName("Message")
    private String errorMsg;

    public String getErrorMsg() {
        return errorMsg != null ? errorMsg : "";
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @SerializedName("List1")
    private List<UserPreferences> preferencesList;

    public List<UserPreferences> getPreferencesList() {
        return preferencesList;
    }

    public void setPreferencesList(List<UserPreferences> preferencesList) {
        this.preferencesList = preferencesList;
    }

    public String getNewOTP() {
        return newOTP;
    }

    public void setNewOTP(String newOTP) {
        this.newOTP = newOTP;
    }

    public String getResponse() {
        return Response;
    }

    public void setResponse(String response) {
        Response = response;
    }

    private String newOTP;
    private String Response;

    @SerializedName(Constant.w_user_profile_image_name)
    private String w_user_profile_image_name;

    public String getW_user_profile_image_name() {
        return w_user_profile_image_name;
    }

    public void setW_user_profile_image_name(String w_user_profile_image_name) {
        this.w_user_profile_image_name = w_user_profile_image_name;
    }
}
