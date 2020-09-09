package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

/**
 * Created by Pritali on 6/8/2020.
 */
public class FriendsResponse {

    private String Message;

    @SerializedName(Constant.Response)
    private String response;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    @SerializedName(Constant.row_count)
    private String row_count;


    @SerializedName(Constant.connection_list)
    private List<Friends> friends_list;

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

    public List<Friends> getFriends_list() {
        return friends_list;
    }

    public void setFriends_list(List<Friends> connection_list) {
        this.friends_list = connection_list;
    }
}
