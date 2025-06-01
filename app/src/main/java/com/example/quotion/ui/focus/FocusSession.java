package com.example.quotion.ui.focus;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//Model cho 1 gửi về dữ liệu
public class FocusSession {
    public long startTime, endTime;
    public String date;

    // Constructor
    public FocusSession(){}
    public FocusSession(long startTime, long endTime){
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = getDateString(startTime);
    }

    private String getDateString(long millis){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
    public long getDuration(){
        return endTime - startTime;

    }
}
