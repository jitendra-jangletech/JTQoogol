package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

/**
 * Created by Pritali on 2/1/2020.
 */

public class University
{

    @SerializedName(Constant.ubm_board_name)
    private String name;

    @SerializedName(Constant.ubm_id)
    private String univBoardId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnivBoardId() {
        return univBoardId;
    }

    public void setUnivBoardId(String univBoardId) {
        this.univBoardId = univBoardId;
    }
}

