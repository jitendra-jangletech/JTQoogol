package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TestListResponse {

    @SerializedName("Response")
    private String response;

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

    @SerializedName("Message")
    private String message;

    @SerializedName("List1")
    private List<TestModelNew> testList;

    public List<TestModelNew> getTestList() {
        return testList;
    }

    public void setTestList(List<TestModelNew> testList) {
        this.testList = testList;
    }
}
