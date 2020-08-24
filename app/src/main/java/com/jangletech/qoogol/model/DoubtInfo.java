package com.jangletech.qoogol.model;

import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.AESSecurities;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import java.util.Locale;
import java.util.concurrent.Executor;

/**
 * Created by Pritali on 8/21/2020.
 */

@Entity
public class DoubtInfo {

    @PrimaryKey
    @NonNull
    @SerializedName(Constant.crms_id)
    private String doubt_id;

    @SerializedName(Constant.u_user_id)
    private String user_id;

    @SerializedName(Constant.q_id)
    private String question_id;

    @SerializedName(Constant.u_first_name_encrypted)
    private String first_name;

    @SerializedName(Constant.u_last_name_encrypted)
    private String last_name;

    @SerializedName(Constant.crms_cdatetime)
    private String date_time;

    @SerializedName(Constant.crms_likes)
    private String doubt_like;

    @SerializedName(Constant.crms_views)
    private String doubt_views;

    @SerializedName(Constant.mst_text)
    private String doubt_text;

    @SerializedName(Constant.w_user_profile_image_name)
    private String profile;


    public String getDoubt_id() {
        return doubt_id!=null?doubt_id:"";
    }

    public void setDoubt_id(String doubt_id) {
        this.doubt_id = doubt_id;
    }

    public String getUser_id() {
        return user_id!=null?user_id:"";
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFirst_name() {
        return first_name!=null?first_name:"";
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name!=null?last_name:"";
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getDate_time() {
        return date_time!=null?date_time:"";
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getDoubt_like() {
        return doubt_like!=null?doubt_like:"";
    }

    public void setDoubt_like(String doubt_like) {
        this.doubt_like = doubt_like;
    }

    public String getDoubt_views() {
        return doubt_views!=null?doubt_views:"";
    }

    public void setDoubt_views(String doubt_views) {
        this.doubt_views = doubt_views;
    }

    public String getDoubt_text() {
        return doubt_text!=null? AppUtils.decodedString(doubt_text) :"";
    }

    public void setDoubt_text(String doubt_text) {
        this.doubt_text = doubt_text;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getName() {
        return String.format(Locale.ENGLISH, "%s %s", AESSecurities.getInstance().decrypt(first_name), AESSecurities.getInstance().decrypt(last_name));
    }

}
