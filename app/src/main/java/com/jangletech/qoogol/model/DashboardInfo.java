package com.jangletech.qoogol.model;

/**
 * Created by Pritali on 3/9/2020.
 */
public class DashboardInfo {
    private String[] extraAttributeList;

    private String message;

    private String statusCode;

    private DashboardData object;

    public String[] getExtraAttributeList ()
    {
        return extraAttributeList;
    }

    public void setExtraAttributeList (String[] extraAttributeList)
    {
        this.extraAttributeList = extraAttributeList;
    }

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getStatusCode ()
    {
        return statusCode;
    }

    public void setStatusCode (String statusCode)
    {
        this.statusCode = statusCode;
    }

    public Object getObject ()
    {
        return object;
    }

    public void setObject (DashboardData object)
    {
        this.object = object;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [extraAttributeList = "+extraAttributeList+", message = "+message+", statusCode = "+statusCode+", object = "+object+"]";
    }
}
