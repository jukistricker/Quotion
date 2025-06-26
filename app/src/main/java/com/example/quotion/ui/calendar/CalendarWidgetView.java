package com.example.quotion.ui.calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

import com.example.quotion.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarWidgetView extends FrameLayout {

    private TextView monthText;
    private TextView yearText;
    private LinearLayout daysContainer;
    private ImageView leftArrow, rightArrow;
    private Calendar currentCalendar;
    private Calendar displayCalendar;

    // Colors - you can customize these
    private int primaryColor = Color.parseColor("#809CFF");
    private int secondaryColor = Color.parseColor("#2196F3");
    private int tertiaryColor = Color.parseColor("#424242");
    private int errorColor = Color.parseColor("#F44336");
    private int grayColor = Color.parseColor("#9E9E9E");

    public CalendarWidgetView(Context context) {
        super(context);
        init();
    }

    public CalendarWidgetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CalendarWidgetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public interface OnDayClickListener {
        void onDayClick(Calendar date);
    }

    private OnDayClickListener onDayClickListener;

    public void setOnDayClickListener(OnDayClickListener listener) {
        this.onDayClickListener = listener;
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.calendar_widget_layout, this, true);

        currentCalendar = Calendar.getInstance();
        displayCalendar = (Calendar) currentCalendar.clone();

        moveToStartOfWeek(); // <-- Khởi tạo displayCalendar là đầu tuần hiện tại

        findViews();
        setupClickListeners();
        updateCalendar();
    }


    private void findViews() {
        monthText = findViewById(R.id.monthText);
        yearText = findViewById(R.id.yearText);
        daysContainer = findViewById(R.id.daysContainer);
        leftArrow = findViewById(R.id.leftArrow);
        rightArrow = findViewById(R.id.rightArrow);
    }

    private void setupClickListeners() {
        leftArrow.setOnClickListener(v -> {
            displayCalendar.add(Calendar.WEEK_OF_YEAR, -1);
            moveToStartOfWeek(); // Đảm bảo luôn là đầu tuần
            updateCalendar();
        });

        rightArrow.setOnClickListener(v -> {
            displayCalendar.add(Calendar.WEEK_OF_YEAR, 1);
            moveToStartOfWeek(); // Đảm bảo luôn là đầu tuần
            updateCalendar();
        });

    }

    private void updateCalendar() {
        updateDays();
    }


    private void updateMonthYear(Calendar referenceDate) {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

        String month = monthFormat.format(referenceDate.getTime()).toUpperCase();
        String year = yearFormat.format(referenceDate.getTime());

        monthText.setText(month);
        yearText.setText(year);
    }


    private void updateDays() {
        daysContainer.removeAllViews();

        Calendar weekStart = (Calendar) displayCalendar.clone();

        for (int i = 0; i < 7; i++) {
            Calendar dayCalendar = (Calendar) weekStart.clone();
            dayCalendar.add(Calendar.DAY_OF_MONTH, i);

            View dayView = createDayView(dayCalendar);
            daysContainer.addView(dayView);
        }

        updateMonthYear(weekStart);
    }


    private View selectedDayContainer = null;

    private View createDayView(Calendar dayCalendar) {
        View dayView = LayoutInflater.from(getContext()).inflate(R.layout.calendar_day_item, null);

        TextView dayNameText = dayView.findViewById(R.id.dayNameText);
        TextView dayNumberText = dayView.findViewById(R.id.dayNumberText);
        View eventIndicator = dayView.findViewById(R.id.eventIndicator);
        FrameLayout dayContainer = dayView.findViewById(R.id.dayContainer);

        // Set day name
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        String dayName = dayFormat.format(dayCalendar.getTime()).toUpperCase();
        dayNameText.setText(dayName);

        // Set day number
        int dayNumber = dayCalendar.get(Calendar.DAY_OF_MONTH);
        dayNumberText.setText(String.valueOf(dayNumber));

        // Check if it's today
        boolean isToday = isSameDay(dayCalendar, currentCalendar);

        // Check if it's weekend
        int dayOfWeek = dayCalendar.get(Calendar.DAY_OF_WEEK);
        boolean isWeekend = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);

        // Style the day
        if (isToday) {
            setTodayBackground(dayContainer);
            dayNameText.setTextColor(Color.WHITE);
            dayNumberText.setTextColor(Color.WHITE);
            // Nếu bạn muốn mặc định ngày hôm nay là được chọn, bạn có thể set selectedDayContainer = dayContainer ở đây
        } else {
            setDayBackground(dayContainer, tertiaryColor);
            if (isWeekend) {
                dayNameText.setTextColor(errorColor);
            } else {
                dayNameText.setTextColor(Color.parseColor("#DDFFFFFF"));
            }
            dayNumberText.setTextColor(Color.parseColor("#DDFFFFFF"));
        }

        // Show event indicator
        if (hasEvent(dayCalendar)) {
            eventIndicator.setVisibility(View.VISIBLE);
            GradientDrawable indicatorDrawable = new GradientDrawable();
            indicatorDrawable.setShape(GradientDrawable.OVAL);
            indicatorDrawable.setColor(Color.rgb(128,156,255));
            eventIndicator.setBackground(indicatorDrawable);
        } else {
            eventIndicator.setVisibility(View.GONE);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(4, 0, 4, 0);
        dayView.setLayoutParams(params);

        dayView.setOnClickListener(v -> {
            // Reset ngày đã chọn trước đó (nếu có)
            if (selectedDayContainer != null) {
                // Reset background ngày trước
                // Nếu ngày trước là hôm nay, giữ màu todayBackground, ngược lại về tertiaryColor
                Calendar oldSelectedDate = (Calendar) selectedDayContainer.getTag(R.id.tag_date);
                if (oldSelectedDate != null && isSameDay(oldSelectedDate, currentCalendar)) {
                    setTodayBackground(selectedDayContainer);
                    // Set màu text trắng
                    setDayBackground(dayContainer, tertiaryColor);
                    TextView oldDayName = selectedDayContainer.findViewById(R.id.dayNameText);
                    TextView oldDayNumber = selectedDayContainer.findViewById(R.id.dayNumberText);
                    oldDayName.setTextColor(Color.WHITE);
                    oldDayNumber.setTextColor(Color.WHITE);
                } else {
                    setDayBackground(selectedDayContainer, tertiaryColor);
                    setDayBackground(dayContainer, tertiaryColor);
                    TextView oldDayName = selectedDayContainer.findViewById(R.id.dayNameText);
                    TextView oldDayNumber = selectedDayContainer.findViewById(R.id.dayNumberText);
                    int oldDayOfWeek = oldSelectedDate != null ? oldSelectedDate.get(Calendar.DAY_OF_WEEK) : -1;
                    if (oldDayOfWeek == Calendar.SATURDAY || oldDayOfWeek == Calendar.SUNDAY) {
                        oldDayName.setTextColor(errorColor);
                    } else {
                        oldDayName.setTextColor(Color.parseColor("#DDFFFFFF"));
                    }
                    oldDayNumber.setTextColor(Color.parseColor("#DDFFFFFF"));
                }
            }

            // Set background ngày được chọn hiện tại
            setDayBackground(dayContainer, Color.rgb(128,156,255));
            dayNameText.setTextColor(Color.WHITE);
            dayNumberText.setTextColor(Color.WHITE);

            selectedDayContainer = dayContainer;

            // Gọi callback onDayClickListener
            if (onDayClickListener != null) {
                onDayClickListener.onDayClick(dayCalendar);
            }
        });

        // Lưu ngày vào tag để khi reset dễ lấy lại info
        dayContainer.setTag(R.id.tag_date, dayCalendar);

        return dayView;
    }

    private void setDayBackground(View view, int color) {
        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
        background.setColor(color);
        background.setCornerRadius(8f);
        view.setBackground(background);
    }
    private void setTodayBackground(View view) {
        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
        background.setColor(Color.rgb(128,156,255));
        background.setCornerRadius(8f);
        view.setBackground(background);
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean hasEvent(Calendar dayCalendar) {
        // Example logic: show indicator on weekdays
        int dayOfWeek = dayCalendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.THURSDAY || dayOfWeek == Calendar.SATURDAY;
    }

    // Method to refresh the calendar (call this when you want to update to current time)
    public void refreshCalendar() {
        currentCalendar = Calendar.getInstance();
        displayCalendar = (Calendar) currentCalendar.clone();
        updateCalendar();
    }

    // Setters for colors
    public void setPrimaryColor(int color) {
        this.primaryColor = color;
        updateCalendar();
    }

    public void setSecondaryColor(int color) {
        this.secondaryColor = color;
        updateCalendar();
    }

    public void setTertiaryColor(int color) {
        this.tertiaryColor = color;
        updateCalendar();
    }
    private void moveToStartOfWeek() {
        int firstDayOfWeek = displayCalendar.getFirstDayOfWeek(); // Thường là SUNDAY

        while (displayCalendar.get(Calendar.DAY_OF_WEEK) != firstDayOfWeek) {
            displayCalendar.add(Calendar.DAY_OF_MONTH, -1);
        }
    }

}