package com.jangletech.qoogol.model;

/**
 * Created by Pritali on 3/9/2020.
 */
public class DashboardData {
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

    @Override
    public String toString()
    {
        return "ClassPojo [favQA = "+favQA+", creditPoints = "+creditPoints+", followers = "+followers+", examQs = "+examQs+", courseQs = "+courseQs+", following = "+following+", favTests = "+favTests+", createdTests = "+createdTests+", avgRating = "+avgRating+", attendedTests = "+attendedTests+", friends = "+friends+"]";
    }
}
