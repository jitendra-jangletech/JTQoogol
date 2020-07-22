package com.jangletech.qoogol.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

public class QSet {

    @SerializedName("QSet")
    private String qSet;

    @SerializedName(Constant.q_id)
    private int q_id;

    @SerializedName(Constant.q_quest)
    private String q_quest;

    @SerializedName(Constant.q_quest_desc)
    private String q_quest_desc;

    @SerializedName(Constant.q_type)
    private String q_type;

    @SerializedName(Constant.q_media_type)
    private String q_media_type;

   /* @SerializedName(Constant.q_options_type)
    private String q_options_type;*/

    protected QSet(Parcel in) {
        qSet = in.readString();
        q_id = in.readInt();
        q_quest = in.readString();
        q_quest_desc = in.readString();
        q_type = in.readString();
        q_media_type = in.readString();
        //q_options_type = in.readString();
    }

    public String getqSet() {
        return qSet;
    }

    public void setqSet(String qSet) {
        this.qSet = qSet;
    }

    public int getQ_id() {
        return q_id;
    }

    public void setQ_id(int q_id) {
        this.q_id = q_id;
    }

    public String getQ_quest() {
        return q_quest;
    }

    public void setQ_quest(String q_quest) {
        this.q_quest = q_quest;
    }

    public String getQ_quest_desc() {
        return q_quest_desc != null ? q_quest_desc : "";
    }

    public void setQ_quest_desc(String q_quest_desc) {
        this.q_quest_desc = q_quest_desc;
    }

    public String getQ_type() {
        return q_type;
    }

    public void setQ_type(String q_type) {
        this.q_type = q_type;
    }

    public String getQ_media_type() {
        return q_media_type;
    }

    public void setQ_media_type(String q_media_type) {
        this.q_media_type = q_media_type;
    }

   /* public String getQ_options_type() {
        return q_options_type;
    }

    public void setQ_options_type(String q_options_type) {
        this.q_options_type = q_options_type;
    }*/
}
