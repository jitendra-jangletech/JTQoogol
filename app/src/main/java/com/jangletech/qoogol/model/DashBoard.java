package com.jangletech.qoogol.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import org.jetbrains.annotations.NotNull;

@Entity
public class DashBoard {

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @NotNull
    @PrimaryKey
    private String userId;

    @SerializedName(Constant.u_first_name)
    private String firstName;

    @SerializedName(Constant.u_last_name)
    private String lastName;

    @SerializedName(Constant.w_user_profile_image_name)
    private String profilePicUrl;

    @SerializedName(Constant.u_conn_count)
    private String connectionCount;

    @SerializedName(Constant.u_dash_followers)
    private String followers;

    @SerializedName(Constant.u_dash_followings)
    private String followings;

    @SerializedName(Constant.u_friends)
    private String u_friends;

    @SerializedName(Constant.up_credits)
    private String up_credits;

    @SerializedName(Constant.up_fav_q)
    private String up_fav_q;

    @SerializedName(Constant.up_fav_tests )
    private String up_fav_tests;

    @SerializedName(Constant.up_q_attempts)
    private String up_q_attempts;

    @SerializedName(Constant.up_q_likes)
    private String up_q_likes ;

    @SerializedName(Constant.up_q_ratings_given)
    private String up_q_ratings_given;

    @SerializedName(Constant.up_q_right_answers)
    private String up_q_right_answers;

    @SerializedName(Constant.up_q_shares)
    private String up_q_shares;

    @SerializedName(Constant.up_rank)
    private String up_rank;

    @SerializedName(Constant.up_tests_likes)
    private String up_tests_likes;

    @SerializedName(Constant.up_tests_ratings_given)
    private String up_tests_ratings_given;

    @SerializedName(Constant.Message)
    private String response;

    @SerializedName(Constant.Response)
    private String message;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getConnectionCount() {
        return connectionCount;
    }

    public void setConnectionCount(String connectionCount) {
        this.connectionCount = connectionCount;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getFollowings() {
        return followings;
    }

    public void setFollowings(String followings) {
        this.followings = followings;
    }

    public String getU_friends() {
        return u_friends;
    }

    public void setU_friends(String u_friends) {
        this.u_friends = u_friends;
    }

    public String getUp_credits() {
        return up_credits;
    }

    public void setUp_credits(String up_credits) {
        this.up_credits = up_credits;
    }

    public String getUp_fav_q() {
        return up_fav_q;
    }

    public void setUp_fav_q(String up_fav_q) {
        this.up_fav_q = up_fav_q;
    }

    public String getUp_fav_tests() {
        return up_fav_tests;
    }

    public void setUp_fav_tests(String up_fav_tests) {
        this.up_fav_tests = up_fav_tests;
    }

    public String getUp_q_attempts() {
        return up_q_attempts;
    }

    public void setUp_q_attempts(String up_q_attempts) {
        this.up_q_attempts = up_q_attempts;
    }

    public String getUp_q_likes() {
        return up_q_likes;
    }

    public void setUp_q_likes(String up_q_likes) {
        this.up_q_likes = up_q_likes;
    }

    public String getUp_q_ratings_given() {
        return up_q_ratings_given;
    }

    public void setUp_q_ratings_given(String up_q_ratings_given) {
        this.up_q_ratings_given = up_q_ratings_given;
    }

    public String getUp_q_right_answers() {
        return up_q_right_answers;
    }

    public void setUp_q_right_answers(String up_q_right_answers) {
        this.up_q_right_answers = up_q_right_answers;
    }

    public String getUp_q_shares() {
        return up_q_shares;
    }

    public void setUp_q_shares(String up_q_shares) {
        this.up_q_shares = up_q_shares;
    }

    public String getUp_rank() {
        return up_rank;
    }

    public void setUp_rank(String up_rank) {
        this.up_rank = up_rank;
    }

    public String getUp_tests_likes() {
        return up_tests_likes;
    }

    public void setUp_tests_likes(String up_tests_likes) {
        this.up_tests_likes = up_tests_likes;
    }

    public String getUp_tests_ratings_given() {
        return up_tests_ratings_given;
    }

    public void setUp_tests_ratings_given(String up_tests_ratings_given) {
        this.up_tests_ratings_given = up_tests_ratings_given;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
