package com.jangletech.qoogol.model.signup;

public class SignUpRequestDto {

    private String firstName;
    private String lastName;
    private String email;
    private String mobileNo;
    private String password;
    private int country;
    private int state;
    private int board;
    private int institute;
    private int degree;
    private int course;
    private int cyNum;
    private Boolean isMobileVerified;
    private Boolean isEmailVerified;
    private int City;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCountry() {
        return country;
    }

    public void setCountry(int country) {
        this.country = country;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getBoard() {
        return board;
    }

    public void setBoard(int board) {
        this.board = board;
    }

    public int getInstitute() {
        return institute;
    }

    public void setInstitute(int institute) {
        this.institute = institute;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public int getCyNum() {
        return cyNum;
    }

    public void setCyNum(int cyNum) {
        this.cyNum = cyNum;
    }

    public Boolean getMobileVerified() {
        return isMobileVerified;
    }

    public void setMobileVerified(Boolean mobileVerified) {
        isMobileVerified = mobileVerified;
    }

    public Boolean getEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public int getCity() {
        return City;
    }

    public void setCity(int city) {
        City = city;
    }

}
