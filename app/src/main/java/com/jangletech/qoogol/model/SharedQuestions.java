package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.io.Serializable;

/**
 * Created by Pritali on 7/20/2020.
 */
public class SharedQuestions implements Serializable {

    @SerializedName(Constant.stq_id)
    private String stq_id;

    @SerializedName(Constant.stq_by_user_id)
    private String stq_by_user_id;

    @SerializedName(Constant.stq_to_user_id)
    private String stq_to_user_id;

    @SerializedName(Constant.stq_to_cr_id)
    private String stq_to_cr_id;

    @SerializedName(Constant.stq_tm_id)
    private String stq_tm_id;

    @SerializedName(Constant.stq_q_id)
    private String stq_q_id;

    @SerializedName(Constant.stq_cdatetime)
    private String stq_cdatetime;

    @SerializedName(Constant.stq_mst_id)
    private String stq_mst_id;

    @SerializedName(Constant.u_first_name)
    private String u_first_name;

    @SerializedName(Constant.u_last_name)
    private String u_last_name;

    @SerializedName(Constant.group_name)
    private String group_name;

    @SerializedName(Constant.w_user_profile_image_name)
    private String w_user_profile_image_name;

    public String getU_first_name() {
        return u_first_name;
    }

    public void setU_first_name(String u_first_name) {
        this.u_first_name = u_first_name;
    }

    public String getU_last_name() {
        return u_last_name;
    }

    public void setU_last_name(String u_last_name) {
        this.u_last_name = u_last_name;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getW_user_profile_image_name() {
        return w_user_profile_image_name;
    }

    public void setW_user_profile_image_name(String w_user_profile_image_name) {
        this.w_user_profile_image_name = w_user_profile_image_name;
    }

    public String getStq_id() {
        return stq_id;
    }

    public void setStq_id(String stq_id) {
        this.stq_id = stq_id;
    }

    public String getStq_by_user_id() {
        return stq_by_user_id;
    }

    public void setStq_by_user_id(String stq_by_user_id) {
        this.stq_by_user_id = stq_by_user_id;
    }

    public String getStq_to_user_id() {
        return stq_to_user_id;
    }

    public void setStq_to_user_id(String stq_to_user_id) {
        this.stq_to_user_id = stq_to_user_id;
    }

    public String getStq_to_cr_id() {
        return stq_to_cr_id;
    }

    public void setStq_to_cr_id(String stq_to_cr_id) {
        this.stq_to_cr_id = stq_to_cr_id;
    }

    public String getStq_tm_id() {
        return stq_tm_id;
    }

    public void setStq_tm_id(String stq_tm_id) {
        this.stq_tm_id = stq_tm_id;
    }

    public String getStq_q_id() {
        return stq_q_id;
    }

    public void setStq_q_id(String stq_q_id) {
        this.stq_q_id = stq_q_id;
    }

    public String getStq_cdatetime() {
        return stq_cdatetime!=null?"Shared on : " +stq_cdatetime.substring(0, 10):"";
    }

    public void setStq_cdatetime(String stq_cdatetime) {
        this.stq_cdatetime = stq_cdatetime;
    }

    public String getStq_mst_id() {
        return stq_mst_id;
    }

    public void setStq_mst_id(String stq_mst_id) {
        this.stq_mst_id = stq_mst_id;
    }
}
