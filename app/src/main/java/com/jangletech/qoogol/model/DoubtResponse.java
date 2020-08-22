package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

/**
 * Created by Pritali on 8/21/2020.
 */
public class DoubtResponse {
    @SerializedName(Constant.Response)
    private String response;

    @SerializedName("Message")
    private String message;

    @SerializedName("List1")
    private List<DoubtInfo> doubtInfoList;

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

    public List<DoubtInfo> getDoubtInfoList() {
        return doubtInfoList;
    }

    public void setDoubtInfoList(List<DoubtInfo> doubtInfoList) {
        this.doubtInfoList = doubtInfoList;
    }
}
