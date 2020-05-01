package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;
import java.util.List;

/**
 * Created by Pritali on 4/27/2020.
 */
public class UniversityResponse {
    @SerializedName(Constant.Response)
    private String response;

    @SerializedName(Constant.masterDataList)
    private List<University> masterDataList;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<University> getMasterDataList() {
        return masterDataList;
    }

    public void setMasterDataList(List<University> masterDataList) {
        this.masterDataList = masterDataList;
    }
}
