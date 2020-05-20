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

public enum Gender {

    Male("M"),
    Female("F"),
    Other("O");

    public String value;

    Gender(String value) {
        this.value = value;
    }


    public static String constantValue(String value) {
        Gender[] genders = Gender.values();

        for (Gender gender : genders) {
            if (gender.getValue().equalsIgnoreCase(value)) {
                return gender.name();
            }
        }

        return value;
    }

    public String getValue() {
        return value;
    }

}
