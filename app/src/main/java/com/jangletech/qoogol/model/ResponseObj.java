package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

/**
 * Created by Pritali on 4/28/2020.
 */
public class ResponseObj {
    @SerializedName(Constant.Response)
    private String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
