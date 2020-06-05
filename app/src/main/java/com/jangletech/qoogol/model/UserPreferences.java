package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

public class UserPreferences {

    @SerializedName(Constant.clm_cy)
    private String classId;

    @SerializedName(Constant.clm_class_name)
    private String className;

    @SerializedName(Constant.u_user_id)
    private String userId;

    @SerializedName(Constant.u_filter_matches)
    private String u_filter_matches;

    @SerializedName(Constant.ubm_id)
    private String ubm_id;

    @SerializedName(Constant.ubm_board_name)
    private String ubm_board_name;

    @SerializedName(Constant.iom_id)
    private String iom_id;

    @SerializedName(Constant.iom_name)
    private String iom_name;

    @SerializedName(Constant.dm_id)
    private String dm_id;

    @SerializedName(Constant.dm_degree_name)
    private String dm_degree_name;

    @SerializedName(Constant.co_name)
    private String co_name;

    @SerializedName(Constant.co_id)
    private String co_id;

    @SerializedName(Constant.DataList)
    private String dataList;

    @SerializedName(Constant.question_list)
    private String examList;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDataList() {
        return dataList;
    }

    public void setDataList(String dataList) {
        this.dataList = dataList;
    }

    public String getExamList() {
        return examList;
    }

    public void setExamList(String examList) {
        this.examList = examList;
    }

    public String getDm_degree_name() {
        return dm_degree_name;
    }

    public void setDm_degree_name(String dm_degree_name) {
        this.dm_degree_name = dm_degree_name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getU_filter_matches() {
        return u_filter_matches;
    }

    public void setU_filter_matches(String u_filter_matches) {
        this.u_filter_matches = u_filter_matches;
    }

    public String getUbm_id() {
        return ubm_id;
    }

    public void setUbm_id(String ubm_id) {
        this.ubm_id = ubm_id;
    }

    public String getUbm_board_name() {
        return ubm_board_name;
    }

    public void setUbm_board_name(String ubm_board_name) {
        this.ubm_board_name = ubm_board_name;
    }

    public String getIom_id() {
        return iom_id;
    }

    public void setIom_id(String iom_id) {
        this.iom_id = iom_id;
    }

    public String getIom_name() {
        return iom_name;
    }

    public void setIom_name(String iom_name) {
        this.iom_name = iom_name;
    }

    public String getDm_id() {
        return dm_id;
    }

    public void setDm_id(String dm_id) {
        this.dm_id = dm_id;
    }

    public String getCo_name() {
        return co_name;
    }

    public void setCo_name(String co_name) {
        this.co_name = co_name;
    }

    public String getCo_id() {
        return co_id;
    }

    public void setCo_id(String co_id) {
        this.co_id = co_id;
    }

}
