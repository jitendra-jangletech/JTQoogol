package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

public class UserPreferenceResponse {

    @SerializedName(Constant.co_name)
    private String courseName;

    public String getCourseName() {
        return courseName!=null?courseName:"";
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDegreeName() {
        return degreeName!=null?degreeName:"";
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }

    public String getStartDate() {
        return startDate!=null?startDate:"";
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate!=null?endDate:"";
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getClassYear() {
        return classYear!=null?classYear:"";
    }

    public void setClassYear(String classYear) {
        this.classYear = classYear;
    }

    public String getBoardName() {
        return boardName!=null?boardName:"";
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getInstituteName() {
        return instituteName!=null?instituteName:"";
    }

    public void setInstituteName(String instituteName) {
        this.instituteName = instituteName;
    }

    @SerializedName(Constant.dm_degree_name)
    private String degreeName;

    @SerializedName(Constant.ue_startdate)
    private String startDate;

    @SerializedName(Constant.ue_enddate)
    private String endDate;

    @SerializedName(Constant.ue_cy_num)
    private String classYear;

    @SerializedName(Constant.ubm_board_name)
    private String boardName;

    @SerializedName(Constant.iom_name)
    private String instituteName;

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
