package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

public class FetchSubjectResponse {

    @SerializedName(Constant.scr_sm_id)
    private String scr_sm_id;

    @SerializedName(Constant.scr_co_id)
    private String scr_co_id;

    @SerializedName(Constant.scr_ex_id)
    private String scr_ex_id;

    @SerializedName(Constant.scr_sem_id)
    private String scr_sem_id;

    @SerializedName(Constant.scr_cy_num)
    private String scr_cy_num;

    @SerializedName(Constant.sm_sub_name)
    private String sm_sub_name;

    @SerializedName(Constant.co_name)
    private String co_name;

    @SerializedName(Constant.ex_exam_name)
    private String ex_exam_name;

    public String getScr_sm_id() {
        return scr_sm_id;
    }

    public void setScr_sm_id(String scr_sm_id) {
        this.scr_sm_id = scr_sm_id;
    }

    public String getScr_co_id() {
        return scr_co_id;
    }

    public void setScr_co_id(String scr_co_id) {
        this.scr_co_id = scr_co_id;
    }

    public String getScr_ex_id() {
        return scr_ex_id;
    }

    public void setScr_ex_id(String scr_ex_id) {
        this.scr_ex_id = scr_ex_id;
    }

    public String getScr_sem_id() {
        return scr_sem_id;
    }

    public void setScr_sem_id(String scr_sem_id) {
        this.scr_sem_id = scr_sem_id;
    }

    public String getScr_cy_num() {
        return scr_cy_num;
    }

    public void setScr_cy_num(String scr_cy_num) {
        this.scr_cy_num = scr_cy_num;
    }

    public String getSm_sub_name() {
        return sm_sub_name;
    }

    public void setSm_sub_name(String sm_sub_name) {
        this.sm_sub_name = sm_sub_name;
    }

    public String getCo_name() {
        return co_name;
    }

    public void setCo_name(String co_name) {
        this.co_name = co_name;
    }

    public String getEx_exam_name() {
        return ex_exam_name;
    }

    public void setEx_exam_name(String ex_exam_name) {
        this.ex_exam_name = ex_exam_name;
    }
}
