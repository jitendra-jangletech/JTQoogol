package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

public class ClassList {

    private String Message;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    private String Response;

    public String getResponse() {
        return Response;
    }

    public void setResponse(String response) {
        Response = response;
    }

    public List<ClassResponse> getClassResponseList() {
        return classResponseList;
    }

    public void setClassResponseList(List<ClassResponse> classResponseList) {
        this.classResponseList = classResponseList;
    }

    @SerializedName("List1")
    private List<ClassResponse> classResponseList;
}



