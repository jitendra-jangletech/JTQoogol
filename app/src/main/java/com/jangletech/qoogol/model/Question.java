package com.jangletech.qoogol.model;

public class Question {

    private int id;

    public Question(int id, String strQuestion, String strQuestInfo) {
        this.id = id;
        this.strQuestion = strQuestion;
        this.strQuestInfo = strQuestInfo;
    }

    private String strQuestion;
    private String strQuestInfo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStrQuestion() {
        return strQuestion;
    }

    public void setStrQuestion(String strQuestion) {
        this.strQuestion = strQuestion;
    }

    public String getStrQuestInfo() {
        return strQuestInfo;
    }

    public void setStrQuestInfo(String strQuestInfo) {
        this.strQuestInfo = strQuestInfo;
    }



}
