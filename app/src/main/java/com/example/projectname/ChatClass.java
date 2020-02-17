package com.example.projectname;

public class ChatClass {

    String senderId;
    String action;
    String message;
    String time;

    public ChatClass(String senderId, String action, String message, String time) {
        this.senderId = senderId;
        this.action = action;
        this.message = message;
        this.time = time;
    }

    public ChatClass() {
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
