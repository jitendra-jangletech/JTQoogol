package com.jangletech.qoogol.model;

import java.util.Locale;

/**
 * Created by Pritali on 7/31/2020.
 */
public class Doubts {

    private String user_id;
    private String first_name;
    private String last_name;
    private String profile;
    private String posted_group;
    private String posted_date;
    private String doubt;
    private String doubt_link;

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPosted_group() {
        return posted_group;
    }

    public void setPosted_group(String posted_group) {
        this.posted_group = posted_group;
    }

    public String getPosted_date() {
        return posted_date;
    }

    public void setPosted_date(String posted_date) {
        this.posted_date = posted_date;
    }

    public String getDoubt() {
        return doubt;
    }

    public void setDoubt(String doubt) {
        this.doubt = doubt;
    }

    public String getDoubt_link() {
        return doubt_link;
    }

    public void setDoubt_link(String doubt_link) {
        this.doubt_link = doubt_link;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getNameWithPage() {
        String postedUserName;
        if (!getPosted_group().isEmpty()) {
            postedUserName = String.format(Locale.ENGLISH, "%s posted on %s",
                    "You", getPosted_group());
        } else {
            postedUserName = getName();
        }
        return postedUserName;
    }

    public String getName() {
        return String.format(Locale.ENGLISH, "%s %s", getFirst_name(), getLast_name());
    }

}
