package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

/**
 * Created by Pritali on 7/23/2020.
 */
public class ShareUserResponse  {

    @SerializedName(Constant.Response)
    private String response;

    @SerializedName(Constant.row_count)
    private String row_count;


    @SerializedName(Constant.question_list)
    private List<SharedQuestions> userList;

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

    public List<SharedQuestions> getUserList() {
        return userList;
    }

    public void setUserList(List<SharedQuestions> userList) {
        this.userList = userList;
    }
}
