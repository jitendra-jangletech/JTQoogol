package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FetchSubjectResponseList {

    @SerializedName("List1")
    private List<FetchSubjectResponse> fetchSubjectResponseList;

    public List<FetchSubjectResponse> getFetchSubjectResponseList() {
        return fetchSubjectResponseList;
    }

    public void setFetchSubjectResponseList(List<FetchSubjectResponse> fetchSubjectResponseList) {
        this.fetchSubjectResponseList = fetchSubjectResponseList;
    }
}
