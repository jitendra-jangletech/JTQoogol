package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;

import static com.jangletech.qoogol.util.Constant.country_id;
import static com.jangletech.qoogol.util.Constant.country_name;

/**
 * Created by Pritali on 1/29/2020.
 */
public class Country {

    @SerializedName(country_id)
    private  int countryId;

    @SerializedName(country_name)
    private String countryName;

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
