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

public enum Relationship {

    Married("M"),
    UnMarried("U"),
    Separated("S"),
    Divorced("D"),
    LiveIn("L"),
    Engaged("E"),
    Couples("C"),
    Single("X");

    public String value;

    Relationship(String value) {
        this.value = value;
    }

    public static String constantValue(String value) {
        Relationship[] relationships = Relationship.values();

        for (Relationship relationship : relationships) {
            if (relationship.getValue().equalsIgnoreCase(value)) {
                return relationship.name();
            }
        }

        return "";
    }

    public String getValue() {
        return value;
    }

}
