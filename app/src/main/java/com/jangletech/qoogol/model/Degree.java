package com.jangletech.qoogol.model;

/**
 * Created by Pritali on 2/1/2020.
 */

public class Degree
{
    private int degreeId;

    private String stream;

    private String level;

    private String name;

    public int getDegreeId ()
    {
        return degreeId;
    }

    public void setDegreeId (int degreeId)
    {
        this.degreeId = degreeId;
    }

    public String getStream ()
    {
        return stream;
    }

    public void setStream (String stream)
    {
        this.stream = stream;
    }

    public String getLevel ()
    {
        return level;
    }

    public void setLevel (String level)
    {
        this.level = level;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [degreeId = "+degreeId+", stream = "+stream+", level = "+level+", name = "+name+"]";
    }
}

