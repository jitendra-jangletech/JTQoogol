package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import static com.jangletech.qoogol.util.Constant.country_id;
import static com.jangletech.qoogol.util.Constant.country_name;

/**
 * Created by Pritali on 1/29/2020.
 */
public class Country {

    @SerializedName(Constant.c_id)
    private  int countryId;

    @SerializedName(Constant.c_name)
    private String countryName;

    @SerializedName(Constant.c_calling_code)
    private String callingCode;

    @SerializedName(Constant.c_abbr_2)
    private String abbrevation;

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

    public String getCallingCode() {
        return callingCode;
    }

    public void setCallingCode(String callingCode) {
        this.callingCode = callingCode;
    }

    public String getAbbrevation() {
        return abbrevation;
    }

    public void setAbbrevation(String abbrevation) {
        this.abbrevation = abbrevation;
    }
}
