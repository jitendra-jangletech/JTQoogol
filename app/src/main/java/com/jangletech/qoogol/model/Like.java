package com.jangletech.qoogol.model;

import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

/**
 * Created by Pritali on 4/29/2020.
 */
public class Like {

    public String getTlcUserId() {
        return tlcUserId;
    }

    public void setTlcUserId(String tlcUserId) {
        this.tlcUserId = tlcUserId;
    }

    @PrimaryKey
    @SerializedName(Constant.qlc_id)
    private String likeId;

    @SerializedName(Constant.tlc_user_id)
    private String tlcUserId;

    @SerializedName(Constant.qlc_user_id)
    private String userId;

   /* @SerializedName(Constant.u_first_name)
    private String userFirstName;

    @SerializedName(Constant.u_last_name)
    private String userLastName;*/

    @SerializedName(Constant.u_first_name_encrypted)
    private String userFirstName;

    @SerializedName(Constant.u_last_name_encrypted)
    private String userLastName;

    @SerializedName(Constant.qlc_cdatetime)
    private String time;

    @SerializedName(Constant.qlc_q_id)
    private String question_id;

    @SerializedName(Constant.qlc_like_flag)
    private String isLiked;

    @SerializedName(Constant.w_user_profile_image_name)
    private String profile_image;

    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserFirstName() {
        return userFirstName != null ? userFirstName : "";
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName != null ? userLastName : "";
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(String isLiked) {
        this.isLiked = isLiked;
    }

    public String getProfile_image() {
        return profile_image != null ? profile_image : "";
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getUserName() {
        return userFirstName == null && userLastName == null ? "" : userFirstName + " " + userLastName;
    }
}