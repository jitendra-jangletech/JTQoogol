package com.jangletech.qoogol.model;

/**
 * Created by Pritali on 2/1/2020.
 */

public class Course
{
    private int duration;

    private String degreeId;

    private String name;

    private String courseId;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDegreeId ()
    {
        return degreeId;
    }

    public void setDegreeId (String degreeId)
    {
        this.degreeId = degreeId;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getCourseId ()
    {
        return courseId;
    }

    public void setCourseId (String courseId)
    {
        this.courseId = courseId;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [duration = "+duration+", degreeId = "+degreeId+", name = "+name+", courseId = "+courseId+"]";
    }
}

