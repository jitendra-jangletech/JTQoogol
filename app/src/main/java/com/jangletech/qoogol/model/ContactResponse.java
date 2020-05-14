package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

/**
 * Created by Pritali on 5/11/2020.
 */
public class ContactResponse {
    @SerializedName(Constant.Response)
    private String response;

    @SerializedName(Constant.row_count)
    private String pagefetch;

    @SerializedName(Constant.GroupMembersList)
    private List<Contacts> contactList;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<Contacts> getContactList() {
        return contactList;
    }

    public void setContactList(List<Contacts> contactList) {
        this.contactList = contactList;
    }

    public String getPagefetch() {
        return pagefetch;
    }

    public void setPagefetch(String pagefetch) {
        this.pagefetch = pagefetch;
    }
}
