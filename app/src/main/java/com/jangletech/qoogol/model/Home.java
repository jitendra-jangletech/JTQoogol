package com.jangletech.qoogol.model;

/**
 * Created by Pritali on 1/27/2020.
 */
public class Home {
    String courses, questions, exams;

    public Home(String courses, String questions, String exams) {
        this.courses = courses;
        this.questions = questions;
        this.exams = exams;
    }

    public String getCourses() {
        return courses;
    }

    public void setCourses(String courses) {
        this.courses = courses;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getExams() {
        return exams;
    }

    public void setExams(String exams) {
        this.exams = exams;
    }
}
