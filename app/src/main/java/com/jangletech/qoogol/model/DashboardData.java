package com.jangletech.qoogol.model;

/**
 * Created by Pritali on 3/9/2020.
 */
public class DashboardData {

    private String answers;
    private String questions;
    private String tests;

    private String avg_user;
    private String feed_tests;
    private String course;
    private String exam;

    private String favQA;

    private String creditPoints;

    private String followers;

    private String examQs;

    private String courseQs;

    private String following;

    private String favTests;

    private String createdTests;

    private String avgRating;

    private String attendedTests;

    private String friends;

    public String getFavQA ()
    {
        return favQA;
    }

    public void setFavQA (String favQA)
    {
        this.favQA = favQA;
    }

    public String getCreditPoints ()
    {
        return creditPoints;
    }

    public void setCreditPoints (String creditPoints)
    {
        this.creditPoints = creditPoints;
    }

    public String getFollowers ()
    {
        return followers;
    }

    public void setFollowers (String followers)
    {
        this.followers = followers;
    }

    public String getExamQs ()
    {
        return examQs;
    }

    public void setExamQs (String examQs)
    {
        this.examQs = examQs;
    }

    public String getCourseQs ()
    {
        return courseQs;
    }

    public void setCourseQs (String courseQs)
    {
        this.courseQs = courseQs;
    }

    public String getFollowing ()
    {
        return following;
    }

    public void setFollowing (String following)
    {
        this.following = following;
    }

    public String getFavTests ()
    {
        return favTests;
    }

    public void setFavTests (String favTests)
    {
        this.favTests = favTests;
    }

    public String getCreatedTests ()
    {
        return createdTests;
    }

    public void setCreatedTests (String createdTests)
    {
        this.createdTests = createdTests;
    }

    public String getAvgRating ()
    {
        return avgRating;
    }

    public void setAvgRating (String avgRating)
    {
        this.avgRating = avgRating;
    }

    public String getAttendedTests ()
    {
        return attendedTests;
    }

    public void setAttendedTests (String attendedTests)
    {
        this.attendedTests = attendedTests;
    }

    public String getFriends ()
    {
        return friends;
    }

    public void setFriends (String friends)
    {
        this.friends = friends;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getTests() {
        return tests;
    }

    public void setTests(String tests) {
        this.tests = tests;
    }

    public String getAvg_user() {
        return avg_user;
    }

    public void setAvg_user(String avg_user) {
        this.avg_user = avg_user;
    }

    public String getFeed_tests() {
        return feed_tests;
    }

    public void setFeed_tests(String feed_tests) {
        this.feed_tests = feed_tests;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getExam() {
        return exam;
    }

    public void setExam(String exam) {
        this.exam = exam;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [favQA = "+favQA+", creditPoints = "+creditPoints+", followers = "+followers+", examQs = "+examQs+", courseQs = "+courseQs+", following = "+following+", favTests = "+favTests+", createdTests = "+createdTests+", avgRating = "+avgRating+", attendedTests = "+attendedTests+", friends = "+friends+"]";
    }
}
