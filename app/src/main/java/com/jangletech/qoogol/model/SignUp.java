package com.jangletech.qoogol.model;

/**
 * Created by Pritali on 2/11/2020.
 */
public class SignUp
{
    private String message;

    private String statusCode;

    private Object object;

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

    public void setObject (Object object)
    {
        this.object = object;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = "+message+", statusCode = "+statusCode+", object = "+object+"]";
    }
}

