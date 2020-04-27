package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

/**
 * Created by Pritali on 2/1/2020.
 */
public class Institute
{

    @SerializedName(Constant.iom_name)
    private String name;

    @SerializedName(Constant.iom_id)
    private String instOrgId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstOrgId() {
        return instOrgId;
    }

    public void setInstOrgId(String instOrgId) {
        this.instOrgId = instOrgId;
    }
}

