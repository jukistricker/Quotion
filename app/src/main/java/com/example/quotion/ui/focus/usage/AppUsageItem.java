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
}
