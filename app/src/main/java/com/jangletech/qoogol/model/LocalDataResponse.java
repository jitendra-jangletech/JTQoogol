package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

/**
 * Created by Pritali on 8/25/2020.
 */
public class LocalDataResponse {

    @SerializedName(Constant.Response)
    private String response;

    @SerializedName("questions")
    private List<LearningQuestionsNew> questionDataList;

    @SerializedName("tests")
    private List<TestModelNew> testDataList;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<LearningQuestionsNew> getQuestionDataList() {
        return questionDataList;
    }

    public void setQuestionDataList(List<LearningQuestionsNew> questionDataList) {
        this.questionDataList = questionDataList;
    }

    public List<TestModelNew> getTestDataList() {
        return testDataList;
    }

    public void setTestDataList(List<TestModelNew> testDataList) {
        this.testDataList = testDataList;
    }
}
