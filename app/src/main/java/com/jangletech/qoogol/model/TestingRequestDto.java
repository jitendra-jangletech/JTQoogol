package com.jangletech.qoogol.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TestingRequestDto {


    @SerializedName("240")
    @Expose(serialize = false)
    private String tt_id;

    @SerializedName("241")
    @Expose(serialize = false)
    private String tt_tm_id;

    @SerializedName("101")
    @Expose(serialize = true)
    private String u_user_id;


    public String getTt_id() {
        return tt_id;
    }

    public void setTt_id(String tt_id) {
        this.tt_id = tt_id;
    }

    public String getTt_tm_id() {
        return tt_tm_id;
    }

    public void setTt_tm_id(String tt_tm_id) {
        this.tt_tm_id = tt_tm_id;
    }

    public String getU_user_id() {
        return u_user_id;
    }

    public void setU_user_id(String u_user_id) {
        this.u_user_id = u_user_id;
    }



}
