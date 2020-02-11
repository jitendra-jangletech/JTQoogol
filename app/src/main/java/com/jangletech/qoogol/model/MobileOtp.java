package com.jangletech.qoogol.model;

/**
 * Created by Pritali on 2/7/2020.
 */

public class MobileOtp
{
    private String message;

    private String statusCode;

    private String object;

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

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = "+message+", statusCode = "+statusCode+", object = "+object+"]";
    }
}
