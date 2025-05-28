package com.example.quotion.ui.task;

public class Task {
    public String title;
    public String description;
    public String startTime;
    public String color;
    public String userId;
    public String category;

    public Task() {}  // Firebase needs this

    public Task(String title, String description, String startTime, String color, String userId,String category) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.color = color;
        this.userId = userId;
        this.category=category;
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

