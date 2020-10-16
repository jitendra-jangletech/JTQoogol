package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

public class CreateTestResponse {

    @SerializedName("70E")
    private List<TestQuestionNew> testQuestionNewList;

    public List<TestQuestionNew> getTestQuestionNewList() {
        return testQuestionNewList;
    }

    public void setTestQuestionNewList(List<TestQuestionNew> testQuestionNewList) {
        this.testQuestionNewList = testQuestionNewList;
    }

    @SerializedName("Response")
    private int response;

    @SerializedName("Message")
    private String message;

    @SerializedName(Constant.tm_id)
    private String tmId;

    public String getTmId() {
        return tmId;
    }

    public void setTmId(String tmId) {
        this.tmId = tmId;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
