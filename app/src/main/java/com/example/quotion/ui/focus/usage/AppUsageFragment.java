package com.example.quotion.ui.focus.usage;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quotion.databinding.FragmentAppUsageBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppUsageFragment extends Fragment {

    private FragmentAppUsageBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAppUsageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestUsageStatsPermissionIfNeeded();
        loadAppUsageStats();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (hasUsageStatsPermission()) {
            loadAppUsageStats();
        }
    }
    private boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager) requireContext().getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), requireContext().getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }


    private void requestUsageStatsPermissionIfNeeded() {
        AppOpsManager appOps = (AppOpsManager) requireContext().getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), requireContext().getPackageName());

        if (mode != AppOpsManager.MODE_ALLOWED) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            Toast.makeText(requireContext(), "Vui lòng cấp quyền Usage Access", Toast.LENGTH_LONG).show();
        }
    }

    private void loadAppUsageStats() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) requireContext().getSystemService(Context.USAGE_STATS_SERVICE);

        PackageManager pm = requireContext().getPackageManager();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long startTime = calendar.getTimeInMillis();
        long endTime = System.currentTimeMillis();

        //-----------log thời gian ----------///
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        Log.d(TAG, "Truy vấn dữ liệu từ: " + sdf.format(new Date(startTime)) + " (millis: " + startTime + ")");
        Log.d(TAG, "Đến: " + sdf.format(new Date(endTime)) + " (millis: " + endTime + ")");
//---------------------------------------------------------//

        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        if (stats == null || stats.isEmpty()) {
            Log.w(TAG, "Không có dữ liệu UsageStats trả về từ UsageStatsManager hoặc danh sách trống.");
            Toast.makeText(requireContext(), "Không có dữ liệu sử dụng", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG, "Tổng số UsageStats entries nhận được: " + stats.size());

        List<AppUsageItem> appUsageItems = new ArrayList<>();

        for (UsageStats stat : stats) {
            long usageTime = stat.getTotalTimeInForeground();
            String packageName = stat.getPackageName();
            if (packageName == null) {
                Log.w(TAG, "Gặp một UsageStats với packageName là null, bỏ qua.");
                continue;
            }

            Log.d(TAG, "--------------------------------------------------");
            Log.d(TAG, "Đang xử lý gói: " + packageName);
            if (usageTime <= 0) {
                Log.d(TAG, "Bỏ qua gói: " + packageName + " do thời gian sử dụng (getTotalTimeInForeground) <= 0.");
                continue;
            }


            String installer = null;
            //-------------------------------------------------------------//
            try {
                installer = pm.getInstallerPackageName(packageName);
                Log.d(TAG, "Package: " + packageName + ", Installer: " + installer);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Lỗi IllegalArgumentException khi lấy Installer cho package: " + packageName + ". Có thể package không còn tồn tại hoặc không hợp lệ.", e);
                // Bỏ qua package không hợp lệ
                continue;
            }
            //---------------------------------------------------------------------//
          if (installer != null ){ //Tránh null pointer

            Log.d("AppUsage", "Package: " + packageName + ", Installer: " + installer);

            try {
                    ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
                    String appName = pm.getApplicationLabel(appInfo).toString();
                    Drawable appIcon = pm.getApplicationIcon(appInfo);

                    appUsageItems.add(new AppUsageItem(appName, usageTime, appIcon));

                Log.i(TAG, "ĐÃ THÊM VÀO DANH SÁCH: AppName: '" + appName + "', Package: " + packageName + ", UsageTime: " + usageTime + "ms");

                } catch (PackageManager.NameNotFoundException e) {

                Log.e(TAG, "Lỗi PackageManager.NameNotFoundException cho package: " + packageName + ". Thông tin ứng dụng không tìm thấy.", e);

//                    e.printStackTrace();
                }
            }
            Log.d(TAG, "--------------------------------------------------");
            if (appUsageItems.isEmpty()) {
                Log.w(TAG, "Danh sách appUsageItems trống sau khi xử lý tất cả các UsageStats.");
            } else {
                Log.i(TAG, "Hoàn tất xử lý. Tổng số ứng dụng trong danh sách appUsageItems: " + appUsageItems.size());
            }
        }

        // Sắp xếp theo thời gian sử dụng giảm dần
        Collections.sort(appUsageItems, (a, b) -> Long.compare(b.getUsageTime(), a.getUsageTime()));

        // Chỉ lấy 5 ứng dụng sử dụng nhiều nhất trong ngày
        List<AppUsageItem> top5Items = appUsageItems.subList(0, Math.min(5, appUsageItems.size()));

        // Gán vào RecyclerView
        AppUsageAdapter adapter = new AppUsageAdapter(top5Items, requireContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
    }
}
