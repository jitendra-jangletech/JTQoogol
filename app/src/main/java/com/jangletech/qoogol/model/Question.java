package com.jangletech.qoogol.model;

public class Question {

    public Question(int id, String strQuestion) {
        this.id = id;
        this.strQuestion = strQuestion;
    }

    private int id;
    private String strQuestion;

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
}
