package com.jangletech.qoogol.model;

public class PracticeQuestion {

    private String question;

    public PracticeQuestion(String question, boolean isContainsMath) {
        this.question = question;
        this.isContainsMath = isContainsMath;
    }

    private boolean isContainsMath;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isContainsMath() {
        return isContainsMath;
    }

    public void setContainsMath(boolean containsMath) {
        isContainsMath = containsMath;
    }


}
