package com.yhjoo.dochef.classes;

public class Notification {
    private final int notificationType;
    private final String userImg;
    private final String userName;
    private final String recipeName;
    private final String dateTime;

    public Notification(int notificationType, String userImg, String userName, String recipeName, String dateTime) {
        this.notificationType = notificationType;
        this.userImg = userImg;
        this.userName = userName;
        this.recipeName = recipeName;
        this.dateTime = dateTime;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public String getUserImg() {
        return userImg;
    }

    public String getUserName() {
        return userName;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public String getDateTime() {
        return dateTime;
    }
}