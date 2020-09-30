package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UploadQuestion implements Serializable{

    private int subjectId;
    private int questionTypeId;
    private String subjectName;
    private String questionType;
    private String questDescription;

    public UploadQuestion(String subjectName, String questDescription) {
        this.subjectName = subjectName;
        this.questDescription = questDescription;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getQuestionTypeId() {
        return questionTypeId;
    }

    public void setQuestionTypeId(int questionTypeId) {
        this.questionTypeId = questionTypeId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestDescription() {
        return questDescription;
    }

    public void setQuestDescription(String questDescription) {
        this.questDescription = questDescription;
    }
}