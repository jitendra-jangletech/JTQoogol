package com.jangletech.qoogol.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

/**
 * Created by Pritali on 5/4/2020.
 */
@Entity
public class Friends {

    @SerializedName(Constant.cn_id)
    private String cn_id;

    @SerializedName(Constant.cn_u2_follows_u1)
    private String cn_u2_follows_u1;

    @SerializedName(Constant.cn_u1_follows_u2)
    private String cn_u1_follows_u2;

    @SerializedName(Constant.cn_blocked_by_u1)
    private String cn_blocked_by_u1;

    @PrimaryKey
    @NonNull
    @SerializedName(Constant.cn_user_id_2)
    private String cn_user_id_2;

    @SerializedName(Constant.cn_connected)
    private String cn_connected;

    @SerializedName(Constant.u_first_name)
    private String u_first_name;

    @SerializedName(Constant.u_last_name)
    private String u_last_name;

    @SerializedName(Constant.u_gender)
    private String u_gender;

    @SerializedName(Constant.u_app_live)
    private String u_app_live;

    @SerializedName(Constant.u_online_status)
    private String u_online_status;

    @SerializedName(Constant.u_status_text)
    private String u_status_text;

    @SerializedName(Constant.u_birth_date)
    private String u_birth_date;

    @SerializedName(Constant.w_datetime)
    private String w_datetime;

    @SerializedName(Constant.u_latest_lat)
    private String u_latest_lat;

    @SerializedName(Constant.u_latest_long)
    private String u_latest_long;

    @SerializedName(Constant.u_conn_count)
    private String u_conn_count;

    @SerializedName(Constant.u_followers)
    private String u_followers;

    @SerializedName(Constant.u_followings)
    private String u_followings;


    @SerializedName(Constant.ucn_count)
    private String ucn_count;


    @SerializedName(Constant.w_u_ms_count)
    private String w_u_ms_count;


    @SerializedName(Constant.w_distance)
    private String w_distance;


    @SerializedName(Constant.w_user_profile_image_name)
    private String prof_pic;

    @SerializedName(Constant.u_user_id)
    private String user_id;

    @SerializedName(Constant.cn_initiated_by_u1)
    private String friend_req_sent;

    @SerializedName(Constant.cn_initiated_by_u2)
    private String friend_req_received;

    @SerializedName(Constant.follow_req_sent)
    private String follow_req_sent;

    @SerializedName(Constant.follow_req_received)
    private String follow_req_received;

    @SerializedName(Constant.RecordType)
    private String RecordType;

    @SerializedName(Constant.group_id)
    private String group_id;

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getRecordType() {
        return RecordType;
    }

    public void setRecordType(String recordType) {
        RecordType = recordType;
    }

    public String getCn_id() {
        return cn_id;
    }

    public void setCn_id(String cn_id) {
        this.cn_id = cn_id;
    }

    public String getCn_u2_follows_u1() {
        return cn_u2_follows_u1;
    }

    public void setCn_u2_follows_u1(String cn_u2_follows_u1) {
        this.cn_u2_follows_u1 = cn_u2_follows_u1;
    }

    public String getCn_u1_follows_u2() {
        return cn_u1_follows_u2;
    }

    public void setCn_u1_follows_u2(String cn_u1_follows_u2) {
        this.cn_u1_follows_u2 = cn_u1_follows_u2;
    }

    public String getCn_blocked_by_u1() {
        return cn_blocked_by_u1 != null ? cn_blocked_by_u1 : "";
    }

    public void setCn_blocked_by_u1(String cn_blocked_by_u1) {
        this.cn_blocked_by_u1 = cn_blocked_by_u1;
    }

    public String getCn_user_id_2() {
        return cn_user_id_2;
    }

    public void setCn_user_id_2(String cn_user_id_2) {
        this.cn_user_id_2 = cn_user_id_2;
    }

    public String getCn_connected() {
        return cn_connected != null ? cn_connected : "";
    }

    public void setCn_connected(String cn_connected) {
        this.cn_connected = cn_connected;
    }

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

    public String getU_gender() {
        return u_gender;
    }

    public void setU_gender(String u_gender) {
        this.u_gender = u_gender;
    }

    public String getU_app_live() {
        return u_app_live;
    }

    public void setU_app_live(String u_app_live) {
        this.u_app_live = u_app_live;
    }

    public String getU_online_status() {
        return u_online_status;
    }

    public void setU_online_status(String u_online_status) {
        this.u_online_status = u_online_status;
    }

    public String getU_status_text() {
        return u_status_text;
    }

    public void setU_status_text(String u_status_text) {
        this.u_status_text = u_status_text;
    }

    public String getU_birth_date() {
        return u_birth_date;
    }

    public void setU_birth_date(String u_birth_date) {
        this.u_birth_date = u_birth_date;
    }

    public String getW_datetime() {
        return w_datetime;
    }

    public void setW_datetime(String w_datetime) {
        this.w_datetime = w_datetime;
    }

    public String getU_latest_lat() {
        return u_latest_lat;
    }

    public void setU_latest_lat(String u_latest_lat) {
        this.u_latest_lat = u_latest_lat;
    }

    public String getU_latest_long() {
        return u_latest_long;
    }

    public void setU_latest_long(String u_latest_long) {
        this.u_latest_long = u_latest_long;
    }

    public String getU_conn_count() {
        return u_conn_count;
    }

    public void setU_conn_count(String u_conn_count) {
        this.u_conn_count = u_conn_count;
    }

    public String getU_followers() {
        return u_followers;
    }

    public void setU_followers(String u_followers) {
        this.u_followers = u_followers;
    }

    public String getU_followings() {
        return u_followings;
    }

    public void setU_followings(String u_followings) {
        this.u_followings = u_followings;
    }

    public String getUcn_count() {
        return ucn_count;
    }

    public void setUcn_count(String ucn_count) {
        this.ucn_count = ucn_count;
    }

    public String getW_u_ms_count() {
        return w_u_ms_count;
    }

    public void setW_u_ms_count(String w_u_ms_count) {
        this.w_u_ms_count = w_u_ms_count;
    }

    public String getW_distance() {
        return w_distance;
    }

    public void setW_distance(String w_distance) {
        this.w_distance = w_distance;
    }

    public String getProf_pic() {
        return prof_pic != null ? prof_pic : "";
    }

    public void setProf_pic(String prof_pic) {
        this.prof_pic = prof_pic;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public String getFriend_req_sent() {
        return friend_req_sent != null ? friend_req_sent : "";
    }

    public void setFriend_req_sent(String friend_req_sent) {
        this.friend_req_sent = friend_req_sent;
    }

    public String getFriend_req_received() {
        return friend_req_received;
    }

    public void setFriend_req_received(String friend_req_received) {
        this.friend_req_received = friend_req_received;
    }

    public String getFollow_req_sent() {
        return follow_req_sent != null ? follow_req_sent : "";
    }

    public void setFollow_req_sent(String follow_req_sent) {
        this.follow_req_sent = follow_req_sent;
    }

    public String getFollow_req_received() {
        return follow_req_received;
    }

    public void setFollow_req_received(String follow_req_received) {
        this.follow_req_received = follow_req_received;
    }
}
