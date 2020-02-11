package com.jangletech.qoogol.model;

/**
 * Created by Pritali on 1/28/2020.
 */
public class UserExamList {
    private String userExamId;

    private String completedDT;

    private String examName;

    private String examId;

    private int rank;

    private int marks;

    private String userId;

    private String status;

    private String completedDTStr;

    public String getUserExamId() {
        return userExamId;
    }

    public void setUserExamId(String userExamId) {
        this.userExamId = userExamId;
    }

    public String getCompletedDT() {
        return completedDT;
    }

    public void setCompletedDT(String completedDT) {
        this.completedDT = completedDT;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompletedDTStr() {
        return completedDTStr;
    }

    public void setCompletedDTStr(String completedDTStr) {
        this.completedDTStr = completedDTStr;
    }

    @Override
    public String toString() {
        return "ClassPojo [userExamId = " + userExamId + ", completedDT = " + completedDT + ", examName = " + examName + ", examId = " + examId + ", rank = " + rank + ", marks = " + marks + ", userId = " + userId + ", status = " + status + ", completedDTStr = " + completedDTStr + "]";
    }
}

