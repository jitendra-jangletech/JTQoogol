package com.jangletech.qoogol.model;

import java.util.List;

/**
 * Created by Pritali on 2/12/2020.
 */
public class Classes
{
    private String message;

    private String statusCode;

    private List<ClassData> object;

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

    public List<ClassData> getObject() {
        return object;
    }

    public void setObject(List<ClassData> object) {
        this.object = object;
    }
}
