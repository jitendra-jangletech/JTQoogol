package com.jangletech.qoogol.model;

/**
 * Created by Pritali on 2/1/2020.
 */

public class University
{
    private String stateId;

    private String name;

    private String univBoardId;

    private int cityId;

    private String countryId;

    public String getStateId ()
    {
        return stateId;
    }

    public void setStateId (String stateId)
    {
        this.stateId = stateId;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getUnivBoardId ()
    {
        return univBoardId;
    }

    public void setUnivBoardId (String univBoardId)
    {
        this.univBoardId = univBoardId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCountryId ()
    {
        return countryId;
    }

    public void setCountryId (String countryId)
    {
        this.countryId = countryId;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [stateId = "+stateId+", name = "+name+", univBoardId = "+univBoardId+", cityId = "+cityId+", countryId = "+countryId+"]";
    }
}

