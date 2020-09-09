package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

/**
 * Created by Pritali on 6/9/2020.
 */
public class FriendRequestResponse {
    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    private String Message;

    @SerializedName(Constant.Response)
    private String response;

    @SerializedName(Constant.row_count)
    private String row_count;


    @SerializedName(Constant.connection_list)
    private List<FriendRequest> friend_req_list;

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

    public List<FriendRequest> getFriend_req_list() {
        return friend_req_list;
    }

    public void setFriend_req_list(List<FriendRequest> friend_req_list) {
        this.friend_req_list = friend_req_list;
    }
}
