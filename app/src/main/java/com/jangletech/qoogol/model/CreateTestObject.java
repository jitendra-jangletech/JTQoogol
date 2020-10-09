package com.jangletech.qoogol.model;

import java.io.Serializable;

public class CreateTestObject implements Serializable {

    private int tmId;
    private int totalMarks;
    private String testDescription;
    private String testTitle;
    private String testDuration;
    private String testSubject;
    private String testSubjectId;
    private String chapters;
    private String diffLevel;
    private String testCategory;

    public int getTmId() {
        return tmId;
    }

    public void setTmId(int tmId) {
        this.tmId = tmId;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public String getTestDescription() {
        return testDescription;
    }

    public void setTestDescription(String testDescription) {
        this.testDescription = testDescription;
    }

    public String getTestTitle() {
        return testTitle;
    }

    public void setTestTitle(String testTitle) {
        this.testTitle = testTitle;
    }

    public String getTestDuration() {
        return testDuration;
    }

    public void setTestDuration(String testDuration) {
        this.testDuration = testDuration;
    }

    public String getTestSubject() {
        return testSubject;
    }

    public void setTestSubject(String testSubject) {
        this.testSubject = testSubject;
    }

    public String getTestSubjectId() {
        return testSubjectId;
    }

    public void setTestSubjectId(String testSubjectId) {
        this.testSubjectId = testSubjectId;
    }

    public String getChapters() {
        return chapters;
    }

    public void setChapters(String chapters) {
        this.chapters = chapters;
    }

    public String getDiffLevel() {
        return diffLevel;
    }

    public void setDiffLevel(String diffLevel) {
        this.diffLevel = diffLevel;
    }

    public String getTestCategory() {
        return testCategory;
    }

    public void setTestCategory(String testCategory) {
        this.testCategory = testCategory;
    }
}
