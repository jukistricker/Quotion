package com.example.quotion.ui.task;

import com.google.firebase.database.Exclude;

public class Task {
    public String title;
    public String description;
    public String startTime;
    public String color;
    public String userId;
    public String category;
    public String key;

    // getter/setter cho key
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

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

    public String getTitle() {
        return  title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String newTitle) {
        this.title=newTitle;
    }

    public void setDescription(String newDesc) {
        this.description=newDesc;
    }

    public void setCategory(String selectedCategory) {
        category=selectedCategory;
    }

    public void setColor(String selectedColorHex) {
        color=selectedColorHex;
    }

}

