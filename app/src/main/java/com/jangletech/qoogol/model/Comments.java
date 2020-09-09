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

    public Comments(String userName, String commentText) {
        this.userFirstName = userName;
        this.tlc_comment_text = commentText;
    }

    @SerializedName(Constant.quest_comment_count)
    private int questCommentCount;

    public int getQuestCommentCount() {
        return questCommentCount;
    }

    public void setQuestCommentCount(int questCommentCount) {
        this.questCommentCount = questCommentCount;
    }

    public int getQuestLikeCount() {
        return questLikeCount;
    }

    public void setQuestLikeCount(int questLikeCount) {
        this.questLikeCount = questLikeCount;
    }

    @SerializedName(Constant.quest_like_count)
    private int questLikeCount;

    @SerializedName(Constant.test_comment_reply_id)
    private int replyCommentId;

    @SerializedName(Constant.like_reply_count)
    private int replyLikeCount;

    public int getReplyLikeCount() {
        return replyLikeCount;
    }

    public void setReplyLikeCount(int replyLikeCount) {
        this.replyLikeCount = replyLikeCount;
    }

    public int getReplyCommentCount() {
        return replyCommentCount;
    }

    public void setReplyCommentCount(int replyCommentCount) {
        this.replyCommentCount = replyCommentCount;
    }

    @SerializedName(Constant.comment_reply_count)
    private int replyCommentCount;

    @PrimaryKey
    @SerializedName(Constant.qlc_id)
    private String commentId;

    @SerializedName(Constant.qlc_comment_text)
    private String comment;

    @SerializedName(Constant.qlc_user_id)
    private String userId;

    @SerializedName(Constant.u_first_name_encrypted)
    private String userFirstName;

    @SerializedName(Constant.u_last_name_encrypted)
    private String userLastName;

    @SerializedName(Constant.qlc_cdatetime)
    private String time;

    @SerializedName(Constant.qlc_q_id)
    private String question_id;

    @SerializedName(Constant.qlc_comment_flag)
    private String isCommented;

    @SerializedName(Constant.w_user_profile_image_name)
    private String profile_image;


    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    /***
     * New Fields Added For Test Comment By Jitendra
     * @return
     */

    private int likeCount;
    private boolean isLiked;


    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    @SerializedName(Constant.tlc_id)
    private String tlc_id;

    @SerializedName(Constant.tlc_user_id)
    private String tlc_user_id;

    @SerializedName(Constant.tlc_tm_id)
    private String tlc_tm_id;

    @SerializedName(Constant.tlc_cdatetime)
    private String tlc_cdatetime;

    @SerializedName(Constant.tlc_comment_flag)
    private String tlc_comment_flag;

    @SerializedName(Constant.tlc_comment_text)
    private String tlc_comment_text;

    public String getTlc_id() {
        return tlc_id;
    }

    public void setTlc_id(String tlc_id) {
        this.tlc_id = tlc_id;
    }

    public String getTlc_user_id() {
        return tlc_user_id;
    }

    public void setTlc_user_id(String tlc_user_id) {
        this.tlc_user_id = tlc_user_id;
    }

    public String getTlc_tm_id() {
        return tlc_tm_id;
    }

    public void setTlc_tm_id(String tlc_tm_id) {
        this.tlc_tm_id = tlc_tm_id;
    }

    public String getTlc_cdatetime() {
        return tlc_cdatetime != null ? tlc_cdatetime : "";
    }

    public void setTlc_cdatetime(String tlc_cdatetime) {
        this.tlc_cdatetime = tlc_cdatetime;
    }

    public String getTlc_comment_flag() {
        return tlc_comment_flag;
    }

    public void setTlc_comment_flag(String tlc_comment_flag) {
        this.tlc_comment_flag = tlc_comment_flag;
    }

    public String getTlc_comment_text() {
        return tlc_comment_text;
    }

    public void setTlc_comment_text(String tlc_comment_text) {
        this.tlc_comment_text = tlc_comment_text;
    }

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
        return time != null ? time : "";
    }

    public void setTime(String time) {
        this.time = time;
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
