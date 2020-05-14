package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

/**
 * Created by Pritali on 5/12/2020.
 */
public class SendInviteResponse {
    @SerializedName(Constant.Response)
    private String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
