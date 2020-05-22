package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

public class Education {

    public String getUe_id() {
        return ue_id;
    }

    public String getUe_user_id() {
        return ue_user_id;
    }

    public String getUe_startdate() {
        return ue_startdate;
    }

    public String getUe_enddate() {
        return ue_enddate;
    }

    public String getUe_marks() {
        return ue_marks;
    }

    public String getUe_grade() {
        return ue_grade;
    }

    public String getUe_dor_id() {
        return ue_dor_id;
    }

    public String getUe_cy_num() {
        return ue_cy_num;
    }

    public String getUbm_board_name() {
        return ubm_board_name;
    }

    public String getIom_name() {
        return iom_name;
    }

    public String getDm_degree_name() {
        return dm_degree_name;
    }

    public String getCo_name() {
        return co_name;
    }

    public String getUbm_id() {
        return ubm_id;
    }

    public String getIom_id() {
        return iom_id;
    }

    public String getCo_id() {
        return co_id;
    }

    public String getDm_id() {
        return dm_id;
    }

    @SerializedName(Constant.ue_id)
    private String ue_id;

    @SerializedName(Constant.ue_user_id)
    private String ue_user_id;

    @SerializedName(Constant.ue_startdate)
    private String ue_startdate;

    @SerializedName(Constant.ue_enddate)
    private String ue_enddate;

    @SerializedName(Constant.ue_marks)
    private String ue_marks;

    @SerializedName(Constant.ue_grade)
    private String ue_grade;

    @SerializedName(Constant.ue_dor_id)
    private String ue_dor_id;

    @SerializedName(Constant.ue_cy_num)
    private String ue_cy_num;

    @SerializedName(Constant.ubm_board_name)
    private String ubm_board_name;

    @SerializedName(Constant.iom_name)
    private String iom_name;

    @SerializedName(Constant.dm_degree_name)
    private String dm_degree_name;

    @SerializedName(Constant.co_name)
    private String co_name;

    @SerializedName(Constant.ubm_id)
    private String ubm_id;

    @SerializedName(Constant.iom_id)
    private String iom_id;

    @SerializedName(Constant.co_id)
    private String co_id;

    @SerializedName(Constant.dm_id)
    private String dm_id;

}
