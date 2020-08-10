package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

public class NotificationResponse {

    @SerializedName("Message")
    private String message;

    @SerializedName("Response")
    private String response;

    @SerializedName(Constant.row_count)
    private String page;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SerializedName("63")
    private List<Notification> notifications;

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
