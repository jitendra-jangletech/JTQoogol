package com.jangletech.qoogol.model;

import java.util.List;

/**
 * Created by Pritali on 1/28/2020.
 */

public class SignInModel {

    private String message;

    private String statusCode;

    private SignInData object;

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

    public SignInData getObject() {
        return object;
    }

    public void setObject(SignInData object) {
        this.object = object;
    }
}

