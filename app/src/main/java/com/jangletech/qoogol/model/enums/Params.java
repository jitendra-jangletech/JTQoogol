package com.jangletech.qoogol.model.enums;

public enum Params {


    COUNTRY("C"),
    STATE("ST"),
    DISTRICT("D"),
    CITY("CT"),


    BOARD("B"),
    COMPANY("CM"),
    COLLEGE("CG"),
    COURSE("CU"),
    DEGREE("DG"),
    COURSEYEAR("CY");



    public String value;

    Params(String value) {
        this.value = value;
    }

}
