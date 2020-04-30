package com.jangletech.qoogol.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

/**
 * Created by Pritali on 4/6/2020.
 */
@Entity
public class Comments {
    @PrimaryKey
    @SerializedName(Constant.qlc_id)
    private String commentId;

    @SerializedName(Constant.qlc_comment_text)
    private String comment;

    @SerializedName(Constant.qlc_user_id)
    private String userId;

    @SerializedName(Constant.u_first_name)
    private String userFirstName;

    @SerializedName(Constant.u_last_name)
    private String userLastName;

    @SerializedName(Constant.qlc_cdatetime)
    private String time;

    @SerializedName(Constant.qlc_q_id)
    private String question_id;

    @SerializedName(Constant.qlc_comment_flag)
    private String isCommented;

    @SerializedName(Constant.w_user_profile_image_name)
    private String profile_image;


    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getIsCommented() {
        return isCommented;
    }

    public void setIsCommented(String isCommented) {
        this.isCommented = isCommented;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}
