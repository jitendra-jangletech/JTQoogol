package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;

public class Country {

    @SerializedName("235")
    private String ct_id = "235";

    @SerializedName("236")
    private String ct_sd_id = "236";

    @SerializedName("237")
    private String ct_dt_id = "237";

    @SerializedName("238")
    private String ct_name = "238";

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
