package com.jangletech.qoogol.model;

/**
 * Created by Pritali on 2/11/2020.
 */
public class SignUp
{
    private String message;

    private String statusCode;

    private SignUpData object;

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

    public SignUpData getObject() {
        return object;
    }

    public void setObject(SignUpData object) {
        this.object = object;
    }
}

