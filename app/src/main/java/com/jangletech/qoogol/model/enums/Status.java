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

public enum Status {

    AttendingClass("i"),
    Away("a"),
    Available("j"),
    Online("o"),
    Busy("b"),
    Cafe("c"),
    Canteen("e"),
    Cooking("k"),
    DoNotDisturb("d"),
    Free("f"),
    Guests("u"),
    Gym("g"),
    Hostel("h"),
    Library("l"),
    Meeting("m"),
    Movie("v"),
    Offline("x"),
    Sleeping("s"),
    Studying("y"),
    Traveling("t"),
    Working("w"),
    Other("z");

    public String value;

    Status(String value) {
        this.value = value;
    }

    public static String constantValue(String value) {
        try {
            Status[] statuses = Status.values();
            for (Status status : statuses) {
                if (status.getValue().equalsIgnoreCase(value)) {
                    return status.name();
                }
            }
        } catch (Exception ex) {
            return "Free";
        }
        return "";
    }

    public String getValue() {
        return value;
    }

}
