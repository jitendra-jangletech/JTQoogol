package com.jangletech.qoogol.model;

/**
 * Created by Pritali on 1/28/2020.
 */
public class UserCourse {
    private String univBrdId;

    private String courseName;

    private String univBrdName;

    private String degreeId;

    private String nextCourseYearName;

    private String courseDuration;

    private String dorId;

    private String nextCourseYearNum;

    private String courseYearName;

    private String degreeName;

    private String courseId;

    private String courseYearNum;

    public String getUnivBrdId() {
        return univBrdId;
    }

    public void setUnivBrdId(String univBrdId) {
        this.univBrdId = univBrdId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getUnivBrdName() {
        return univBrdName;
    }

    public void setUnivBrdName(String univBrdName) {
        this.univBrdName = univBrdName;
    }

    public String getDegreeId() {
        return degreeId;
    }

    public void setDegreeId(String degreeId) {
        this.degreeId = degreeId;
    }

    public String getNextCourseYearName() {
        return nextCourseYearName;
    }

    public void setNextCourseYearName(String nextCourseYearName) {
        this.nextCourseYearName = nextCourseYearName;
    }

    public String getCourseDuration() {
        return courseDuration;
    }

    public void setCourseDuration(String courseDuration) {
        this.courseDuration = courseDuration;
    }

    public String getDorId() {
        return dorId;
    }

    public void setDorId(String dorId) {
        this.dorId = dorId;
    }

    public String getNextCourseYearNum() {
        return nextCourseYearNum;
    }

    public void setNextCourseYearNum(String nextCourseYearNum) {
        this.nextCourseYearNum = nextCourseYearNum;
    }

    public String getCourseYearName() {
        return courseYearName;
    }

    public void setCourseYearName(String courseYearName) {
        this.courseYearName = courseYearName;
    }

    public String getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseYearNum() {
        return courseYearNum;
    }

    public void setCourseYearNum(String courseYearNum) {
        this.courseYearNum = courseYearNum;
    }

    @Override
    public String toString() {
        return "ClassPojo [univBrdId = " + univBrdId + ", courseName = " + courseName + ", univBrdName = " + univBrdName + ", degreeId = " + degreeId + ", nextCourseYearName = " + nextCourseYearName + ", courseDuration = " + courseDuration + ", dorId = " + dorId + ", nextCourseYearNum = " + nextCourseYearNum + ", courseYearName = " + courseYearName + ", degreeName = " + degreeName + ", courseId = " + courseId + ", courseYearNum = " + courseYearNum + "]";
    }
}
