package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

public class StartResumeTestResponse {

    @SerializedName(Constant.tt_id)
    private int ttId;

    @SerializedName(Constant.tt_obtain_marks)
    private String testTakenObtainMarks;

    @SerializedName(Constant.tt_status)
    private String tt_status;

    @SerializedName(Constant.tt_duration_taken)
    private String tt_duration_taken;

    @SerializedName(Constant.tt_comment)
    private String tt_comment;

    @SerializedName(Constant.tt_pause_datetime)
    private String tt_pause_datetime;

    @SerializedName(Constant.tm_id)
    private String tm_id;

    @SerializedName(Constant.tm_sm_id)
    private String tm_sm_id;

    @SerializedName(Constant.tm_cm_id)
    private String tm_cm_id;

    @SerializedName(Constant.tm_up_u_id)
    private String tm_up_u_id;

    @SerializedName(Constant.tm_name)
    private String tm_name;

    @SerializedName(Constant.tm_diff_level)
    private String tm_diff_level;

    @SerializedName(Constant.tm_tot_marks)
    private String tm_tot_marks;

    @SerializedName(Constant.tm_type)
    private String tm_type;

    @SerializedName(Constant.tm_ranking)
    private String tm_ranking;

    @SerializedName(Constant.tm_status)
    private String tm_status;

    @SerializedName(Constant.tm_neg_mks)
    private String tm_neg_mks;

    @SerializedName(Constant.tm_catg)
    private String tm_catg;

    @SerializedName(Constant.tm_category_1)
    private String tm_category_1;

    @SerializedName(Constant.tm_category_2)
    private String tm_category_2;

    @SerializedName(Constant.tm_category_3)
    private String tm_category_3;

    @SerializedName(Constant.tm_avg_rating)
    private String tm_avg_rating;

    @SerializedName(Constant.tm_sharable)
    private String tm_sharable;

    @SerializedName(Constant.tm_comp_quest_count)
    private String tm_comp_quest_count;

    @SerializedName(Constant.tm_duration)
    private String tm_duration;

    @SerializedName(Constant.tm_visibility)
    private String tm_visibility;

    @SerializedName(Constant.cm_chapter_name)
    private String cm_chapter_name;

    @SerializedName(Constant.sm_sub_name)
    private String sm_sub_name;

    @SerializedName("List1")
    private List<TestQuestionNew> testQuestionNewList;

    @SerializedName("Response")
    private String responseCode;

    public int getTtId() {
        return ttId;
    }

    public void setTtId(int ttId) {
        this.ttId = ttId;
    }

    public String getTm_id() {
        return tm_id;
    }

    public void setTm_id(String tm_id) {
        this.tm_id = tm_id;
    }

    public String getTm_sm_id() {
        return tm_sm_id;
    }

    public void setTm_sm_id(String tm_sm_id) {
        this.tm_sm_id = tm_sm_id;
    }

    public String getTm_cm_id() {
        return tm_cm_id;
    }

    public void setTm_cm_id(String tm_cm_id) {
        this.tm_cm_id = tm_cm_id;
    }

    public String getTm_up_u_id() {
        return tm_up_u_id;
    }

    public void setTm_up_u_id(String tm_up_u_id) {
        this.tm_up_u_id = tm_up_u_id;
    }

    public String getTm_name() {
        return tm_name;
    }

    public void setTm_name(String tm_name) {
        this.tm_name = tm_name;
    }

    public String getTm_diff_level() {
        return tm_diff_level;
    }

    public void setTm_diff_level(String tm_diff_level) {
        this.tm_diff_level = tm_diff_level;
    }

    public String getTm_tot_marks() {
        return tm_tot_marks != null ? tm_tot_marks : "";
    }

    public void setTm_tot_marks(String tm_tot_marks) {
        this.tm_tot_marks = tm_tot_marks;
    }

    public String getTm_type() {
        return tm_type;
    }

    public void setTm_type(String tm_type) {
        this.tm_type = tm_type;
    }

    public String getTm_ranking() {
        return tm_ranking;
    }

    public void setTm_ranking(String tm_ranking) {
        this.tm_ranking = tm_ranking;
    }

    public String getTm_status() {
        return tm_status;
    }

    public void setTm_status(String tm_status) {
        this.tm_status = tm_status;
    }

    public String getTm_neg_mks() {
        return tm_neg_mks;
    }

    public void setTm_neg_mks(String tm_neg_mks) {
        this.tm_neg_mks = tm_neg_mks;
    }

    public String getTm_catg() {
        return tm_catg;
    }

    public void setTm_catg(String tm_catg) {
        this.tm_catg = tm_catg;
    }

    public String getTm_category_1() {
        return tm_category_1;
    }

    public void setTm_category_1(String tm_category_1) {
        this.tm_category_1 = tm_category_1;
    }

    public String getTm_category_2() {
        return tm_category_2;
    }

    public void setTm_category_2(String tm_category_2) {
        this.tm_category_2 = tm_category_2;
    }

    public String getTm_category_3() {
        return tm_category_3;
    }

    public void setTm_category_3(String tm_category_3) {
        this.tm_category_3 = tm_category_3;
    }

    public String getTm_avg_rating() {
        return tm_avg_rating;
    }

    public void setTm_avg_rating(String tm_avg_rating) {
        this.tm_avg_rating = tm_avg_rating;
    }

    public String getTm_sharable() {
        return tm_sharable;
    }

    public void setTm_sharable(String tm_sharable) {
        this.tm_sharable = tm_sharable;
    }

    public String getTm_comp_quest_count() {
        return tm_comp_quest_count;
    }

    public void setTm_comp_quest_count(String tm_comp_quest_count) {
        this.tm_comp_quest_count = tm_comp_quest_count;
    }

    public String getTm_duration() {
        return tm_duration!=null?tm_duration:"";
    }

    public void setTm_duration(String tm_duration) {
        this.tm_duration = tm_duration;
    }

    public String getTm_visibility() {
        return tm_visibility;
    }

    public void setTm_visibility(String tm_visibility) {
        this.tm_visibility = tm_visibility;
    }

    public String getCm_chapter_name() {
        return cm_chapter_name;
    }

    public void setCm_chapter_name(String cm_chapter_name) {
        this.cm_chapter_name = cm_chapter_name;
    }

    public String getSm_sub_name() {
        return sm_sub_name;
    }

    public void setSm_sub_name(String sm_sub_name) {
        this.sm_sub_name = sm_sub_name;
    }

    public List<TestQuestionNew> getTestQuestionNewList() {
        return testQuestionNewList;
    }

    public void setTestQuestionNewList(List<TestQuestionNew> testQuestionNewList) {
        this.testQuestionNewList = testQuestionNewList;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }
}
