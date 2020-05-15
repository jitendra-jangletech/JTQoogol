package com.jangletech.qoogol.model;

import android.view.View;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

/*
 *
 *
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *  * //
 *  * //            Copyright (c) 2020. JangleTech Systems Private Limited, Thane, India
 *  * //
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *
 */

public class Contacts {

    @SerializedName(Constant.u_user_id)
    private int userId;

    @SerializedName(Constant.u_first_name)
    private String firstName;

    @SerializedName(Constant.u_last_name)
    private String lastName;

    @SerializedName(Constant.w_contact_name)
    private String userName;

    @SerializedName(Constant.w_contact_number)
    private String mobileNum;

    @SerializedName(Constant.u_mob_1)
    private String databaseMobileNum;

    @SerializedName(Constant.u_calling_code)
    private String callingCode;

    private boolean isAlreadyHaveSpotmeets;

    @SerializedName(Constant.cn_connected)
    private boolean isAlreadyConnected;

    @SerializedName(Constant.cn_blocked_by_u1)
    private boolean blockedByYou;

    @SerializedName(Constant.cn_blocked_by_u2)
    private boolean blockedByOther;

    @SerializedName(Constant.cn_initiated_by_u1)
    private boolean connectionInitiatedByYou;

    @SerializedName(Constant.cn_initiated_by_u2)
    private boolean connectionInitiatedByOther;

    @SerializedName(Constant.cn_request_active)
    private boolean isRequestActive;

    @SerializedName(Constant.w_otp_sent)
    private int otpSent;

    private boolean isSelected = false;

    public String getUserName() {
        String name;
        if (!getNameOnSpotmeetCircle().isEmpty()) {
            name = String.format("%s\n(%s)", userName, getNameOnSpotmeetCircle());
        } else {
            name = userName;
        }
        return name;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobileNum() {
        return mobileNum == null ? "+" + callingCode + databaseMobileNum : mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    public boolean isAlreadyHaveSpotmeets() {
        return isAlreadyHaveSpotmeets;
    }

    public void setAlreadyHaveSpotmeets(boolean alreadyHaveSpotmeets) {
        isAlreadyHaveSpotmeets = alreadyHaveSpotmeets;
    }

    public boolean isAlreadyConnected() {
        return isAlreadyConnected;
    }

    public void setAlreadyConnected(boolean alreadyConnected) {
        isAlreadyConnected = alreadyConnected;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isBlockedByYou() {
        return blockedByYou;
    }

    public void setBlockedByYou(boolean blockedByYou) {
        this.blockedByYou = blockedByYou;
    }

    public String getCallingCode() {
        return callingCode;
    }

    public void setCallingCode(String callingCode) {
        this.callingCode = callingCode;
    }

    public boolean isBlockedByOther() {
        return blockedByOther;
    }

    public void setBlockedByOther(boolean blockedByOther) {
        this.blockedByOther = blockedByOther;
    }

    public String getDatabaseMobileNum() {
        return databaseMobileNum;
    }

    public void setDatabaseMobileNum(String databaseMobileNum) {
        this.databaseMobileNum = databaseMobileNum;
    }

    @NonNull
    @Override
    public String toString() {
        if (isSelected) {
            return getCallingCode() + getDatabaseMobileNum();
        } else {
            return getMobileNum() + "=" + getUserName();
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getNameOnSpotmeetCircle() {
        return firstName == null && lastName == null ? "" : firstName + " " + lastName;
    }

    public String buttonText() {
        String text;
        if (!isAlreadyConnected && !getNameOnSpotmeetCircle().isEmpty() && !isRequestActive()) {
            text = "Connect";
        } else {
            text = "Invite";
        }

        return text;
    }

    public int inviteBtnVisibilty() {
        if (!isAlreadyConnected && !getNameOnSpotmeetCircle().isEmpty() && !isRequestActive()) {
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }

    public int checkboxVisibilty() {
        if (getNameOnSpotmeetCircle().isEmpty() && otpSent == 0) {
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }

    public boolean isOtpAlreadySent() {
        return otpSent != 1;
    }

    public boolean isConnectionInitiatedByOther() {
        return connectionInitiatedByOther;
    }

    public void setConnectionInitiatedByOther(boolean connectionInitiatedByOther) {
        this.connectionInitiatedByOther = connectionInitiatedByOther;
    }

    public boolean isConnectionInitiatedByYou() {
        return connectionInitiatedByYou;
    }

    public void setConnectionInitiatedByYou(boolean connectionInitiatedByYou) {
        this.connectionInitiatedByYou = connectionInitiatedByYou;
    }

    public boolean isRequestActive() {
        return isRequestActive;
    }

    public void setRequestActive(boolean requestActive) {
        isRequestActive = requestActive;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getOtpSent() {
        return otpSent;
    }

    public void setOtpSent(int otpSent) {
        this.otpSent = otpSent;
    }
}