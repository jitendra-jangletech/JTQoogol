package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

public class City {

    @SerializedName(Constant.ct_id)
    private String ct_id;

    @SerializedName(Constant.ct_sd_id)
    private String ct_sd_id;

    @SerializedName(Constant.ct_dt_id)
    private String ct_dt_id;

    @SerializedName(Constant.ct_name)
    private String ct_name;

    public String getCt_id() {
        return ct_id;
    }

    public void setCt_id(String ct_id) {
        this.ct_id = ct_id;
    }

    public String getCt_sd_id() {
        return ct_sd_id;
    }

    public void setCt_sd_id(String ct_sd_id) {
        this.ct_sd_id = ct_sd_id;
    }

    public String getCt_dt_id() {
        return ct_dt_id;
    }

    public void setCt_dt_id(String ct_dt_id) {
        this.ct_dt_id = ct_dt_id;
    }

    public String getCt_name() {
        return ct_name;
    }

    public void setCt_name(String ct_name) {
        this.ct_name = ct_name;
    }

}

