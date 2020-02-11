package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;

import static com.jangletech.qoogol.util.Constant.country_id;
import static com.jangletech.qoogol.util.Constant.state_abbr;
import static com.jangletech.qoogol.util.Constant.state_id;
import static com.jangletech.qoogol.util.Constant.state_name;

/**
 * Created by Pritali on 1/30/2020.
 */
public class State {

    @SerializedName(state_id)
    private  int stateId;

    @SerializedName(state_name)
    private  String stateName;

    @SerializedName(state_abbr)
    private  String stateAbbr;

    @SerializedName(country_id)
    private  int countryId;

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStateAbbr() {
        return stateAbbr;
    }

    public void setStateAbbr(String stateAbbr) {
        this.stateAbbr = stateAbbr;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }
}
