package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

public class UserPreferenceResponse {

    @SerializedName(Constant.selected_ue_id)
    private String selectedUeId;

    @SerializedName(Constant.subjectId)
    private String subjectId;

    @SerializedName(Constant.subjectName)
    private String subjectName;

    public String getChapterId1() {
        return chapterId1 != null ? chapterId1 : "";
    }

    public void setChapterId1(String chapterId1) {
        this.chapterId1 = chapterId1;
    }

    public String getChapterId2() {
        return chapterId2 != null ? chapterId2 : "";
    }

    public void setChapterId2(String chapterId2) {
        this.chapterId2 = chapterId2;
    }

    public String getChapterId3() {
        return chapterId3 != null ? chapterId3 : "";
    }

    public void setChapterId3(String chapterId3) {
        this.chapterId3 = chapterId3;
    }

    public String getChapterName1() {
        return chapterName1 != null ? chapterName1 : "";
    }

    public void setChapterName1(String chapterName1) {
        this.chapterName1 = chapterName1;
    }

    public String getChapterName2() {
        return chapterName2 != null ? chapterName2 : "";
    }

    public void setChapterName2(String chapterName2) {
        this.chapterName2 = chapterName2;
    }

    public String getChapterName3() {
        return chapterName3 != null ? chapterName3 : "";
    }

    public void setChapterName3(String chapterName3) {
        this.chapterName3 = chapterName3;
    }

    @SerializedName(Constant.chapterId1)
    private String chapterId1;

    @SerializedName(Constant.chapterId2)
    private String chapterId2;

    @SerializedName(Constant.chapterId3)
    private String chapterId3;

    @SerializedName(Constant.chapterName1)
    private String chapterName1;

    @SerializedName(Constant.chapterName2)
    private String chapterName2;

    @SerializedName(Constant.chapterName3)
    private String chapterName3;

    @SerializedName(Constant.DataList)
    private List<SyllabusChapter> chapterList;

    @SerializedName(Constant.question_list)
    private List<SyllabusSubject> subjectList;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    @SerializedName("Response")
    private int responseCode;

    public String getSelectedUeId() {
        return selectedUeId != null ? selectedUeId : "";
    }

    public void setSelectedUeId(String selectedUeId) {
        this.selectedUeId = selectedUeId;
    }

    public String getSubjectId() {
        return subjectId != null ? subjectId : "";
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName != null ? subjectName : "";
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public List<SyllabusChapter> getChapterList() {
        return chapterList;
    }

    public void setChapterList(List<SyllabusChapter> chapterList) {
        this.chapterList = chapterList;
    }

    public List<SyllabusSubject> getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(List<SyllabusSubject> subjectList) {
        this.subjectList = subjectList;
    }
}
