package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.io.Serializable;

public class AttemptedTest implements Serializable {

    @SerializedName(Constant.tt_id)
    private int tt_id;

    @SerializedName(Constant.tt_cdatetime)
    private String tt_cdatetime;

    @SerializedName(Constant.tt_obtain_marks)
    private String tt_obtain_marks;

    @SerializedName(Constant.tt_duration_taken)
    private String tt_duration_taken;

    @SerializedName(Constant.tt_pause_datetime)
    private String tt_pause_datetime;

    @SerializedName(Constant.tt_status)
    private String tt_status;

    public int getTt_id() {
        return tt_id;
    }

    public void setTt_id(int tt_id) {
        this.tt_id = tt_id;
    }

    public String getTt_cdatetime() {
        return tt_cdatetime;
    }

    public void setTt_cdatetime(String tt_cdatetime) {
        this.tt_cdatetime = tt_cdatetime;
    }

    public String getTt_obtain_marks() {
        return tt_obtain_marks;
    }

    public void setTt_obtain_marks(String tt_obtain_marks) {
        this.tt_obtain_marks = tt_obtain_marks;
    }

    public String getTt_duration_taken() {
        return tt_duration_taken;
    }

    public void setTt_duration_taken(String tt_duration_taken) {
        this.tt_duration_taken = tt_duration_taken;
    }

    public String getTt_pause_datetime() {
        return tt_pause_datetime;
    }

    public void setTt_pause_datetime(String tt_pause_datetime) {
        this.tt_pause_datetime = tt_pause_datetime;
    }

    public String getTt_status() {
        return tt_status;
    }

    public void setTt_status(String tt_status) {
        this.tt_status = tt_status;
    }
}
