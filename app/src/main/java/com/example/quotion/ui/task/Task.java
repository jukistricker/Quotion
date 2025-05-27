package com.example.quotion.ui.task;

public class Task {
    public String title;
    public String description;
    public String startTime;
    public String color;
    public String userId;

    public Task() {}  // Firebase needs this

    public Task(String title, String description, String startTime, String color, String userId) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.color = color;
        this.userId = userId;
    }

    public void setUserId(String value){
        this.userId=value;
    }
    public void setStartTime(String value){
        this.startTime=value;
    }
    public String getUserId(){
        return userId;
    }
    public String getStartTime(){
        return startTime;
    }
}

