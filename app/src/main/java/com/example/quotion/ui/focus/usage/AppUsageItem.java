package com.example.quotion.ui.focus.usage;

import android.graphics.drawable.Drawable;

public class AppUsageItem {
    private final String appName;
    private final long usageTime;
    private final Drawable appIcon;

    public AppUsageItem(String appName, long usageTime, Drawable appIcon) {
        this.appName = appName;
        this.usageTime = usageTime;
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public long getUsageTime() {
        return usageTime;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }
    public String formatUsageTime(long millis) {
        long totalSeconds = millis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;

        StringBuilder sb = new StringBuilder("You spent ");
        if (hours > 0) {
            sb.append(hours).append(hours == 1 ? " hour " : " hours ");
        }
        sb.append(minutes).append(minutes == 1 ? " minute" : " minutes");

        return sb.toString();
    }
}
