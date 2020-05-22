package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import static com.jangletech.qoogol.util.Constant.s_id;
import static com.jangletech.qoogol.util.Constant.s_name;

public class State {

    @SerializedName(s_id)
    private String state_id;

    @SerializedName(s_name)
    private String stateName;

    public State(String state_id, String stateName) {
        this.state_id = state_id;
        this.stateName = stateName;
    }

    public String getState_id() {
        return state_id;
    }

    public void setState_id(String state_id) {
        this.state_id = state_id;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

}
