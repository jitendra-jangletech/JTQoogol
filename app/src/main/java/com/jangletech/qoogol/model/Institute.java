package com.jangletech.qoogol.model;

/**
 * Created by Pritali on 2/1/2020.
 */
public class Institute
{
    private int stateId;

    private String name;

    private String instOrgId;

    private String univBoardId;

    private String cityId;

    private int countryId;


    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getInstOrgId ()
    {
        return instOrgId;
    }

    public void setInstOrgId (String instOrgId)
    {
        this.instOrgId = instOrgId;
    }

    public String getUnivBoardId ()
    {
        return univBoardId;
    }

    public void setUnivBoardId (String univBoardId)
    {
        this.univBoardId = univBoardId;
    }

    public String getCityId ()
    {
        return cityId;
    }

    public void setCityId (String cityId)
    {
        this.cityId = cityId;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [stateId = "+stateId+", name = "+name+", instOrgId = "+instOrgId+", univBoardId = "+univBoardId+", cityId = "+cityId+", countryId = "+countryId+"]";
    }
}

