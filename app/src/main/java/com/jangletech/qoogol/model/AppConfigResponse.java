package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

public class AppConfigResponse {

    private String Message;
    private String Response;

    @SerializedName(Constant.CF_VIDEO)
    private String cf_video_mb;

    @SerializedName(Constant.CF_IMAGE)
    private String cf_image_mb;

    @SerializedName(Constant.CF_DOC)
    private String cf_doc_mb;

    @SerializedName(Constant.CF_AUDIO)
    private String cf_audio_mb;

    @SerializedName(Constant.cf_key1)
    private String firstNameKey;

    @SerializedName(Constant.cf_key2)
    private String lastNameKey;

    @SerializedName(Constant.cf_key3)
    private String dobKey;

    @SerializedName(Constant.cf_key4)
    private String mobileKey;

    @SerializedName(Constant.cf_key5)
    private String emailKey;

    @SerializedName(Constant.cf_key6)
    private String passwordKey;

    public String getMessage() {
        return Message != null ? Message : "";
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getResponse() {
        return Response != null ? Response : "";
    }

    public void setResponse(String response) {
        Response = response;
    }

    public String getCf_video_mb() {
        return cf_video_mb;
    }

    public void setCf_video_mb(String cf_video_mb) {
        this.cf_video_mb = cf_video_mb;
    }

    public String getCf_image_mb() {
        return cf_image_mb;
    }

    public void setCf_image_mb(String cf_image_mb) {
        this.cf_image_mb = cf_image_mb;
    }

    public String getCf_doc_mb() {
        return cf_doc_mb;
    }

    public void setCf_doc_mb(String cf_doc_mb) {
        this.cf_doc_mb = cf_doc_mb;
    }

    public String getCf_audio_mb() {
        return cf_audio_mb;
    }

    public void setCf_audio_mb(String cf_audio_mb) {
        this.cf_audio_mb = cf_audio_mb;
    }

    public String getFirstNameKey() {
        return firstNameKey != null ? firstNameKey : "";
    }

    public void setFirstNameKey(String firstNameKey) {
        this.firstNameKey = firstNameKey;
    }

    public String getLastNameKey() {
        return lastNameKey != null ? lastNameKey : "";
    }

    public void setLastNameKey(String lastNameKey) {
        this.lastNameKey = lastNameKey;
    }

    public String getDobKey() {
        return dobKey != null ? dobKey : "";
    }

    public void setDobKey(String dobKey) {
        this.dobKey = dobKey;
    }

    public String getMobileKey() {
        return mobileKey != null ? mobileKey : "";
    }

    public void setMobileKey(String mobileKey) {
        this.mobileKey = mobileKey;
    }

    public String getEmailKey() {
        return emailKey != null ? emailKey : "";
    }

    public void setEmailKey(String emailKey) {
        this.emailKey = emailKey;
    }

    public String getPasswordKey() {
        return passwordKey != null ? passwordKey : "";
    }

    public void setPasswordKey(String passwordKey) {
        this.passwordKey = passwordKey;
    }
}
