package com.jangletech.qoogol.model;

import java.util.List;

public class TestSection {

    private String sectionName;
    private int sectionMarks;
    private List<LearningQuestionsNew> sectionQuestions;

    public TestSection(String sectionName, int sectionMarks, List<LearningQuestionsNew> sectionQuestions) {
        this.sectionName = sectionName;
        this.sectionMarks = sectionMarks;
        this.sectionQuestions = sectionQuestions;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public int getSectionMarks() {
        return sectionMarks;
    }

    public void setSectionMarks(int sectionMarks) {
        this.sectionMarks = sectionMarks;
    }

    public List<LearningQuestionsNew> getSectionQuestions() {
        return sectionQuestions;
    }

    public void setSectionQuestions(List<LearningQuestionsNew> sectionQuestions) {
        this.sectionQuestions = sectionQuestions;
    }
}
