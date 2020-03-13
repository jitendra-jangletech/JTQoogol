package com.jangletech.qoogol.model;

import java.util.List;

public class CommonResponseObject {

    private int statusCode;
    private String message;

    public List<UserPersonalDetails> getUserPersonalDetailsList() {
        return userPersonalDetailsList;
    }

    public void setUserPersonalDetailsList(List<UserPersonalDetails> userPersonalDetailsList) {
        this.userPersonalDetailsList = userPersonalDetailsList;
    }

    private List<UserPersonalDetails> userPersonalDetailsList;

    public List<Exams> getExamsList() {
        return examsList;
    }

    public void setExamsList(List<Exams> examsList) {
        this.examsList = examsList;
    }

    private List<Exams> examsList;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
