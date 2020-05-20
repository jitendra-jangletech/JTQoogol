package com.jangletech.qoogol.model.enums;

/*
 *
 *
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *  * //
 *  * //            Copyright (c) 2020. JangleTech Systems Private Limited, Thane, India
 *  * //
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *
 */

public enum Params {

    USER_DETAILS("UD"),

    PROFESSION("P"),
    INDUSTRY_TYPE("IT"),
    LANGUAGE("L"),
    HOBBY("H"),
    PURPOSE("PR"),

    NATIONALITY("N"),
    COUNTRY("C"),
    STATE("ST"),
    DISTRICT("D"),
    CITY("CT"),

    RELIGION("R"),
    SOCIALCOM("S"),

    BOARD("B"),
    COMPANY("CM"),
    COLLEGE("CG"),
    COURSE("CU"),
    DEGREE("DG"),
    COURSEYEAR("CY"),

    UPDATEUSER_DETAILS("UP"),
    GENERATE_MATCHES("GM"),
    APP_LIVE_AUTO("AP"),
    APP_LIVE("A"),
    STATUS_LIVE("ST"),
    ONLINE_STATUS("OS"),
    MESSAGE("M"),
    AUDIO_VIDEO_CALL("CALL"),
    LOGOUT("LOGOUT"),
    LIKE("LIKE"),
    TAGS("TAGS"),
    OTP("otp");

    public String value;

    Params(String value) {
        this.value = value;
    }

}
