package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

/**
 * Created by Pritali on 6/9/2020.
 */
public class BlockedConnResp {
    @SerializedName(Constant.Response)
    private String response;

    @SerializedName(Constant.row_count)
    private String row_count;


    @SerializedName(Constant.connection_list)
    private List<BlockedConnections> blocked_list;

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

    public List<BlockedConnections> getBlocked_list() {
        return blocked_list;
    }

    public void setBlocked_list(List<BlockedConnections> blocked_list) {
        this.blocked_list = blocked_list;
    }
}
