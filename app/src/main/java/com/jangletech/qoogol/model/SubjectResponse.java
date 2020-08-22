package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

import javax.security.auth.Subject;

/**
 * Created by Pritali on 8/20/2020.
 */
public class SubjectResponse {
    @SerializedName(Constant.Response)
    private String response;

    @SerializedName("Message")
    private String message;

    @SerializedName("List1")
    private List<SubjectClass> subjectList;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SubjectClass> getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(List<SubjectClass> subjectList) {
        this.subjectList = subjectList;
    }
}
