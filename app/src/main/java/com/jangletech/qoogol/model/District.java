package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import static com.jangletech.qoogol.util.Constant.dt_id;
import static com.jangletech.qoogol.util.Constant.dt_name;

/*
 *
 *
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *  * //
 *  * //            Copyright (c) 2020. JangleTech Systems Private Limited, Thane, India
 *  * //
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *
 */

public class District {

    @SerializedName(dt_id)
    private String district_id;

    @SerializedName(dt_name)
    private String districtName;

    public District(String district_id, String districtName) {
        this.district_id = district_id;
        this.districtName = districtName;
    }

    public String getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(String district_id) {
        this.district_id = district_id;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }
}
