package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

/**
 * Created by Pritali on 6/8/2020.
 */
public class FollowingResponse {

    private String Message;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    @SerializedName(Constant.Response)
    private String response;

    @SerializedName(Constant.row_count)
    private String row_count;


    @SerializedName(Constant.connection_list)
    private List<Following> following_list;

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

    public List<Following> getFollowing_list() {
        return following_list;
    }

    public void setFollowing_list(List<Following> following_list) {
        this.following_list = following_list;
    }
}
