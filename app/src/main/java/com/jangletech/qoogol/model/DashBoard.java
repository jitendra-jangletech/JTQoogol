package com.jangletech.qoogol.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import org.jetbrains.annotations.NotNull;

@Entity
public class DashBoard {

    @SerializedName(Constant.up_tests_taken)
    private double testTaken;

    public double getTestTaken() {
        return testTaken;
    }

    public void setTestTaken(double testTaken) {
        this.testTaken = testTaken;
    }

    public double getTestShares() {
        return testShares;
    }

    public void setTestShares(double testShares) {
        this.testShares = testShares;
    }

    @SerializedName(Constant.up_tests_shares)
    private double testShares;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    @NotNull
    @PrimaryKey
    private String userId;

    @SerializedName(Constant.u_first_name_encrypted)
    private String firstName;

    @SerializedName(Constant.u_last_name_encrypted)
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
    private double up_fav_q;

    @SerializedName(Constant.up_fav_tests)
    private double up_fav_tests;

    @SerializedName(Constant.up_q_attempts)
    private double up_q_attempts;

    @SerializedName(Constant.up_q_likes)
    private double up_q_likes;

    @SerializedName(Constant.up_q_ratings_given)
    private double up_q_ratings_given;

    @SerializedName(Constant.up_q_right_answers)
    private double up_q_right_answers;

    @SerializedName(Constant.up_q_shares)
    private double up_q_shares;

    @SerializedName(Constant.up_rank)
    private double up_rank;

    @SerializedName(Constant.up_tests_likes)
    private double up_tests_likes;

    @SerializedName(Constant.up_tests_ratings_given)
    private double up_tests_ratings_given;

    @SerializedName(Constant.Message)
    private String message;

    @SerializedName(Constant.Response)
    private int response;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public double getUp_fav_q() {
        return up_fav_q;
    }

    public void setUp_fav_q(double up_fav_q) {
        this.up_fav_q = up_fav_q;
    }

    public double getUp_fav_tests() {
        return up_fav_tests;
    }

    public void setUp_fav_tests(double up_fav_tests) {
        this.up_fav_tests = up_fav_tests;
    }

    public double getUp_q_attempts() {
        return up_q_attempts;
    }

    public void setUp_q_attempts(double up_q_attempts) {
        this.up_q_attempts = up_q_attempts;
    }

    public double getUp_q_likes() {
        return up_q_likes;
    }

    public void setUp_q_likes(double up_q_likes) {
        this.up_q_likes = up_q_likes;
    }

    public double getUp_q_ratings_given() {
        return up_q_ratings_given;
    }

    public void setUp_q_ratings_given(double up_q_ratings_given) {
        this.up_q_ratings_given = up_q_ratings_given;
    }

    public double getUp_q_right_answers() {
        return up_q_right_answers;
    }

    public void setUp_q_right_answers(double up_q_right_answers) {
        this.up_q_right_answers = up_q_right_answers;
    }

    public double getUp_q_shares() {
        return up_q_shares;
    }

    public void setUp_q_shares(double up_q_shares) {
        this.up_q_shares = up_q_shares;
    }

    public double getUp_rank() {
        return up_rank;
    }

    public void setUp_rank(double up_rank) {
        this.up_rank = up_rank;
    }

    public double getUp_tests_likes() {
        return up_tests_likes;
    }

    public void setUp_tests_likes(double up_tests_likes) {
        this.up_tests_likes = up_tests_likes;
    }

    public double getUp_tests_ratings_given() {
        return up_tests_ratings_given;
    }

    public void setUp_tests_ratings_given(double up_tests_ratings_given) {
        this.up_tests_ratings_given = up_tests_ratings_given;
    }

    public String getFirstName() {
        return firstName;
        //return AESSecurities.getInstance().decrypt(firstName);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
        //return AESSecurities.getInstance().decrypt(lastName);
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

}
