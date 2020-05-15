package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;

import static com.jangletech.qoogol.util.Constant.ct_id;
import static com.jangletech.qoogol.util.Constant.ct_name;

public class City {

    @SerializedName(ct_id)
    private String city_id;

    @SerializedName(ct_name)
    private String cityName;


    public City(String city_id, String cityName) {
        this.city_id = city_id;
        this.cityName = cityName;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

}

