package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

/**
 * Created by Pritali on 5/4/2020.
 */
public class ConnectionResponse {
    @SerializedName(Constant.Response)
    private String response;

    @SerializedName(Constant.row_count)
    private String row_count;

    @SerializedName(Constant.connection_list)
    private List<Connections> connection_list;

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

    public List<Connections> getConnection_list() {
        return connection_list;
    }

    public void setConnection_list(List<Connections> connection_list) {
        this.connection_list = connection_list;
    }
}
