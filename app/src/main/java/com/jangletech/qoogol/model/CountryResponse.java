package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

/**
 * Created by Pritali on 4/27/2020.
 */
public class CountryResponse {
    @SerializedName(Constant.Response)
    private String response;

    @SerializedName(Constant.masterDataList)
    private List<Country> masterDataList;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<Country> getMasterDataList() {
        return masterDataList;
    }

    public void setMasterDataList(List<Country> masterDataList) {
        this.masterDataList = masterDataList;
    }
}
