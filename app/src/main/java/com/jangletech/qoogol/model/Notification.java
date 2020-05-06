package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

public class Notification {

    @SerializedName(Constant.cn_connected)
    private String cn_connected;

    @SerializedName(Constant.cn_request_active)
    private String cn_request_active;

    @SerializedName(Constant.cn_initiated_by_u1)
    private String cn_initiated_by_u1;

    @SerializedName(Constant.cn_initiated_by_u2)
    private String cn_initiated_by_u2;

    @SerializedName(Constant.n_id)
    private String n_id;

    @SerializedName(Constant.n_sent_by_u_id)
    private String n_sent_by_u_id;

    @SerializedName(Constant.n_sent_to_u_id)
    private String n_sent_to_u_id;

    @SerializedName(Constant.n_type)
    private String n_type;

    @SerializedName(Constant.n_read_by_user)
    private String n_read_by_user;

    @SerializedName(Constant.w_notification_desc)
    private String w_notification_desc;

    @SerializedName(Constant.n_ref_id)
    private String n_ref_id;

    @SerializedName(Constant.n_ref_type)
    private String n_ref_type;

    @SerializedName(Constant.n_cdatetime)
    private String n_cdatetime;

    @SerializedName(Constant.w_user_profile_image_name)
    private String w_user_profile_image_name;

    public String getCn_connected() {
        return cn_connected;
    }

    public void setCn_connected(String cn_connected) {
        this.cn_connected = cn_connected;
    }

    public String getCn_request_active() {
        return cn_request_active;
    }

    public void setCn_request_active(String cn_request_active) {
        this.cn_request_active = cn_request_active;
    }

    public String getCn_initiated_by_u1() {
        return cn_initiated_by_u1;
    }

    public void setCn_initiated_by_u1(String cn_initiated_by_u1) {
        this.cn_initiated_by_u1 = cn_initiated_by_u1;
    }

    public String getCn_initiated_by_u2() {
        return cn_initiated_by_u2;
    }

    public void setCn_initiated_by_u2(String cn_initiated_by_u2) {
        this.cn_initiated_by_u2 = cn_initiated_by_u2;
    }

    public String getN_id() {
        return n_id;
    }

    public void setN_id(String n_id) {
        this.n_id = n_id;
    }

    public String getN_sent_by_u_id() {
        return n_sent_by_u_id;
    }

    public void setN_sent_by_u_id(String n_sent_by_u_id) {
        this.n_sent_by_u_id = n_sent_by_u_id;
    }

    public String getN_sent_to_u_id() {
        return n_sent_to_u_id;
    }

    public void setN_sent_to_u_id(String n_sent_to_u_id) {
        this.n_sent_to_u_id = n_sent_to_u_id;
    }

    public String getN_type() {
        return n_type;
    }

    public void setN_type(String n_type) {
        this.n_type = n_type;
    }

    public String getN_read_by_user() {
        return n_read_by_user;
    }

    public void setN_read_by_user(String n_read_by_user) {
        this.n_read_by_user = n_read_by_user;
    }

    public String getW_notification_desc() {
        return w_notification_desc;
    }

    public void setW_notification_desc(String w_notification_desc) {
        this.w_notification_desc = w_notification_desc;
    }

    public String getN_ref_id() {
        return n_ref_id;
    }

    public void setN_ref_id(String n_ref_id) {
        this.n_ref_id = n_ref_id;
    }

    public String getN_ref_type() {
        return n_ref_type;
    }

    public void setN_ref_type(String n_ref_type) {
        this.n_ref_type = n_ref_type;
    }

    public String getN_cdatetime() {
        return n_cdatetime;
    }

    public void setN_cdatetime(String n_cdatetime) {
        this.n_cdatetime = n_cdatetime;
    }

    public String getW_user_profile_image_name() {
        return w_user_profile_image_name;
    }

    public void setW_user_profile_image_name(String w_user_profile_image_name) {
        this.w_user_profile_image_name = w_user_profile_image_name;
    }
}
