package com.jangletech.qoogol.model;

/**
 * Created by Pritali on 2/3/2020.
 */
public class ClassData
{
    private String dispText;

    private String value;

    public String getDispText ()
    {
        return dispText;
    }

    public void setDispText (String dispText)
    {
        this.dispText = dispText;
    }

    public String getValue ()
    {
        return value;
    }

    public void setValue (String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [dispText = "+dispText+", value = "+value+"]";
    }
}

