package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

/**
 * Created by Pritali on 6/9/2020.
 */
public class FollowRequestResponse {
    @SerializedName(Constant.Response)
    private String response;

    @SerializedName(Constant.row_count)
    private String row_count;


    @SerializedName(Constant.connection_list)
    private List<FollowRequest> followreq_list;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getRow_count() {
        return row_count;
    }

    public void setRow_count(String row_count) {
        this.row_count = row_count;
    }

    public List<FollowRequest> getFollowreq_list() {
        return followreq_list;
    }

    public void setFollowreq_list(List<FollowRequest> followreq_list) {
        this.followreq_list = followreq_list;
    }
}
