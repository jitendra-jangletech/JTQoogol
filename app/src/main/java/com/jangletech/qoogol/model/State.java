package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;

public class State {

    @SerializedName("225")
    private String s_id;

    @SerializedName("226")
    private String s_c_id;

    @SerializedName("227")
    private String s_name;

    @SerializedName("228")
    private String s_state_abbr;

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public String getS_c_id() {
        return s_c_id;
    }

    public void setS_c_id(String s_c_id) {
        this.s_c_id = s_c_id;
    }

    public String getS_name() {
        return s_name;
    }

    public void setS_name(String s_name) {
        this.s_name = s_name;
    }

    public String getS_state_abbr() {
        return s_state_abbr;
    }

    public void setS_state_abbr(String s_state_abbr) {
        this.s_state_abbr = s_state_abbr;
    }

}
