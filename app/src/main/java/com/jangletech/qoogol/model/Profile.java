package com.jangletech.qoogol.model;

/**
 * Created by Pritali on 2/13/2020.
 */

public class Profile
{
    private String message;

    private String statusCode;

    private ProfileData object;

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

    public ProfileData getObject() {
        return object;
    }

    public void setObject(ProfileData object) {
        this.object = object;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = "+message+", statusCode = "+statusCode+", object = "+object+"]";
    }
}
