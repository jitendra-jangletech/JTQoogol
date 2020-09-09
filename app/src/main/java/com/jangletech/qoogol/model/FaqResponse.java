package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FaqResponse {

    private String Message;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    @SerializedName("70E")
    private List<FAQModel> list;

    private String Response;

    public String getResponse() {
        return Response;
    }

    public void setResponse(String response) {
        Response = response;
    }

    public List<FAQModel> getList() {
        return list;
    }

    public void setList(List<FAQModel> list) {
        this.list = list;
    }

}
