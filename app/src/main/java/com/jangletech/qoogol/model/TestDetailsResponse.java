package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.io.Serializable;
import java.util.List;

public class TestDetailsResponse implements Serializable {

    @SerializedName(Constant.tm_id)
    private String tm_id;

    @SerializedName(Constant.tm_name)
    private String tm_name;

    @SerializedName(Constant.test_description)
    private String test_description;

    @SerializedName(Constant.subject)
    private String subject;

    @SerializedName(Constant.cm_chapter_name)
    private String cm_chapter_name;

    @SerializedName("List1")
    private List<QSet> qSetList;

    @SerializedName("Response")
    private String responseCode;

    public String getTm_id() {
        return tm_id;
    }

    public void setTm_id(String tm_id) {
        this.tm_id = tm_id;
    }

    public String getTm_name() {
        return tm_name;
    }

    public void setTm_name(String tm_name) {
        this.tm_name = tm_name;
    }

    public String getTest_description() {
        return test_description;
    }

    public void setTest_description(String test_description) {
        this.test_description = test_description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCm_chapter_name() {
        return cm_chapter_name;
    }

    public void setCm_chapter_name(String cm_chapter_name) {
        this.cm_chapter_name = cm_chapter_name;
    }

    public List<QSet> getqSetList() {
        return qSetList;
    }

    public void setqSetList(List<QSet> qSetList) {
        this.qSetList = qSetList;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }
}
