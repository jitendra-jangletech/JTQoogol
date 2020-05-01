package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TestListResponse {

    @SerializedName("List1")
    private List<TestModelNew> testList;

    public List<TestModelNew> getTestList() {
        return testList;
    }

    public void setTestList(List<TestModelNew> testList) {
        this.testList = testList;
    }
}
