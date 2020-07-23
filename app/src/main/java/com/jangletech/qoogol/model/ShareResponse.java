package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

/**
 * Created by Pritali on 5/18/2020.
 */
public class ShareResponse {
    @SerializedName(Constant.Response)
    private String response;

    @SerializedName(Constant.row_count)
    private int row_count;


    @SerializedName(Constant.connection_list)
    private List<ShareModel> connection_list;

    public int getRow_count() {
        return row_count;
    }

    public void setRow_count(int row_count) {
        this.row_count = row_count;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<ShareModel> getConnection_list() {
        return connection_list;
    }

    public void setConnection_list(List<ShareModel> connection_list) {
        this.connection_list = connection_list;
    }
}
