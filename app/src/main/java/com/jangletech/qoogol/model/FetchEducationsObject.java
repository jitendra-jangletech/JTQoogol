package com.jangletech.qoogol.model;

public class FetchEducationsObject {

    private int userEduId;
    private String userId;

    public double getStartDate() {
        return startDate;
    }

    public void setStartDate(double startDate) {
        this.startDate = startDate;
    }

    public double getEndDate() {
        return endDate;
    }

    public void setEndDate(double endDate) {
        this.endDate = endDate;
    }

    private double startDate;
    private double endDate;
    private String startDateStr;
    private String endDateStr;
    private int marks;
    private int grade;
    private int dorId;
    private int cyNum;
    private int univBrdId;
    private String univBrdName;
    private int instOrgId;

    public FetchEducationsObject(int userEduId, String userId, double startDate, double endDate, String startDateStr,
                                 String endDateStr, int marks, int grade, int dorId, int cyNum, int univBrdId,
                                 String univBrdName, int instOrgId, String instOrgName, int courseId, String courseName,
                                 int degreeId, String degreeName, int countryId, String countryName, int stateId, String stateName,
                                 int cityId, String cityName) {
        this.userEduId = userEduId;
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startDateStr = startDateStr;
        this.endDateStr = endDateStr;
        this.marks = marks;
        this.grade = grade;
        this.dorId = dorId;
        this.cyNum = cyNum;
        this.univBrdId = univBrdId;
        this.univBrdName = univBrdName;
        this.instOrgId = instOrgId;
        this.instOrgName = instOrgName;
        this.courseId = courseId;
        this.courseName = courseName;
        this.degreeId = degreeId;
        this.degreeName = degreeName;
        this.countryId = countryId;
        this.countryName = countryName;
        this.stateId = stateId;
        this.stateName = stateName;
        this.cityId = cityId;
        this.cityName = cityName;
    }

    private String instOrgName;
    private int courseId;
    private String courseName;
    private int degreeId;
    private String degreeName;
    private int countryId;
    private String countryName;
    private int stateId;
    private String stateName;
    private int cityId;
    private String cityName;

    public int getUserEduId() {
        return userEduId;
    }

    public void setUserEduId(int userEduId) {
        this.userEduId = userEduId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getStartDateStr() {
        return startDateStr;
    }

    public void setStartDateStr(String startDateStr) {
        this.startDateStr = startDateStr;
    }

    public String getEndDateStr() {
        return endDateStr;
    }

    public void setEndDateStr(String endDateStr) {
        this.endDateStr = endDateStr;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getDorId() {
        return dorId;
    }

    public void setDorId(int dorId) {
        this.dorId = dorId;
    }

    public int getCyNum() {
        return cyNum;
    }

    public void setCyNum(int cyNum) {
        this.cyNum = cyNum;
    }

    public int getUnivBrdId() {
        return univBrdId;
    }

    public void setUnivBrdId(int univBrdId) {
        this.univBrdId = univBrdId;
    }

    public String getUnivBrdName() {
        return univBrdName;
    }

    public void setUnivBrdName(String univBrdName) {
        this.univBrdName = univBrdName;
    }

    public int getInstOrgId() {
        return instOrgId;
    }

    public void setInstOrgId(int instOrgId) {
        this.instOrgId = instOrgId;
    }

    public String getInstOrgName() {
        return instOrgName;
    }

    public void setInstOrgName(String instOrgName) {
        this.instOrgName = instOrgName;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getDegreeId() {
        return degreeId;
    }

    public void setDegreeId(int degreeId) {
        this.degreeId = degreeId;
    }

    public String getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }


}
