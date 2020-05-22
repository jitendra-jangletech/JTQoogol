package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FetchEducationResponse {

    @SerializedName("Response")
    private String responseCode;

    public String getResponseCode() {
        return responseCode;
    }

    public List<Education> getEducationList() {
        return educationList;
    }

    @SerializedName("List1")
    private List<Education> educationList;

}
