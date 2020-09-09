package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

public class AddElementResponse {

    private String Message;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    @SerializedName("520")
    private String instituteId;

    public String getInstituteId() {
        return instituteId;
    }

    public void setInstituteId(String instituteId) {
        this.instituteId = instituteId;
    }

    @SerializedName(Constant.ubm_id)
    private String ubm_id;

    @SerializedName(Constant.iom_id)
    private String iom_id;

    @SerializedName(Constant.iom_name)
    private String iom_name;

    private String Response;

    public String getIom_id() {
        return iom_id;
    }

    public void setIom_id(String iom_id) {
        this.iom_id = iom_id;
    }

    public String getIom_name() {
        return iom_name;
    }

    public void setIom_name(String iom_name) {
        this.iom_name = iom_name;
    }

    public String getUbm_id() {
        return ubm_id;
    }

    public void setUbm_id(String ubm_id) {
        this.ubm_id = ubm_id;
    }

    public String getResponse() {
        return Response;
    }

    public void setResponse(String response) {
        Response = response;
    }
}
