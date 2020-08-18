package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

public class TestListResponse {

    private String prev_tm_id;

    public String getPrev_tm_id() {
        return prev_tm_id;
    }

    public void setPrev_tm_id(String prev_tm_id) {
        this.prev_tm_id = prev_tm_id;
    }

//    @SerializedName(Constant.row_count)
//    private String pageCount;
//
//    public String getPageCount() {
//        return pageCount;
//    }
//
//    public void setPageCount(String pageCount) {
//        this.pageCount = pageCount;
//    }

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
