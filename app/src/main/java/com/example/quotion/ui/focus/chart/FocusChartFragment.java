package com.example.quotion.ui.focus.chart;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.quotion.databinding.FragmentFocusChartBinding;
import com.example.quotion.ui.focus.FocusViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FocusChartFragment extends Fragment {

    private FragmentFocusChartBinding binding;
    private FocusViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFocusChartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(FocusViewModel.class);

        viewModel.dailyFocusDurations.observe(getViewLifecycleOwner(), map -> {
            if (map == null || map.isEmpty()) {
                Toast.makeText(requireContext(), "Không có dữ liệu focus", Toast.LENGTH_SHORT).show();
            }
            updateChart(map);
        });

        viewModel.loadFocusSessionsForChart();
    }

    private void updateChart(Map<String, Long> focusMap) {
        if (focusMap == null || focusMap.isEmpty()) return;

        String[] days = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // Lấy ngày hôm nay (SUN, MON,...)
        String todayLabel = new SimpleDateFormat("EEE", Locale.US).format(new Date()).toUpperCase();

        for (int i = 0; i < days.length; i++) {
            String day = days[i];
            long duration = focusMap.getOrDefault(day, 0L);
            float hours = duration / (1000f * 60f * 60f);
            entries.add(new BarEntry(i, hours));
            labels.add(day);
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        // Gán màu: ngày hôm nay = tím, còn lại = xám
        List<Integer> colors = new ArrayList<>();
        for (String day : days) {
            if (day.equals(todayLabel)) {
                colors.add(Color.parseColor("#878CFF")); // Tím
            } else {
                colors.add(Color.parseColor("#999999")); // Xám
            }
        }
        dataSet.setColors(colors);

        // Hiển thị text trên đầu cột: 4h30m
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int hours = (int) value;
                int minutes = Math.round((value - hours) * 60);
                if (hours == 0 && minutes == 0) return "";
                return hours + "h" + (minutes > 0 ? minutes + "m" : "");
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.7f);

        BarChart chart = binding.barChart;
        chart.setData(barData);
        chart.setFitBars(true);
        chart.setDrawValueAboveBar(true);
        chart.setBackgroundColor(Color.BLACK);

        // Trục X
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setTextColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        // Trục Y trái
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(Color.WHITE);

        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.invalidate();
    }
}
