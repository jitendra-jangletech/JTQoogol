package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

public class ClassResponse {

    @SerializedName(Constant.clm_class_name)
    private String className;

    @SerializedName(Constant.clm_co_id)
    private String clm_co_id;

    @SerializedName(Constant.clm_cy)
    private String clm_cy;

    @SerializedName(Constant.clm_class_num)
    private String clm_class_num;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClm_co_id() {
        return clm_co_id;
    }

    public void setClm_co_id(String clm_co_id) {
        this.clm_co_id = clm_co_id;
    }

    public String getClm_cy() {
        return clm_cy;
    }

    public void setClm_cy(String clm_cy) {
        this.clm_cy = clm_cy;
    }

    public String getClm_class_num() {
        return clm_class_num;
    }

    public void setClm_class_num(String clm_class_num) {
        this.clm_class_num = clm_class_num;
    }
}
