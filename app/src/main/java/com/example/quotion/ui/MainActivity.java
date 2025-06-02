package com.example.quotion.ui;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.quotion.receiver.TaskAlarmReceiver;
import com.example.quotion.utils.ColorSpinnerAdapter;
import com.example.quotion.R;
import com.example.quotion.ui.calendar.CalendarFragment;
import com.example.quotion.ui.focus.FocusFragment;
import com.example.quotion.ui.home.HomeFragment;
import com.example.quotion.ui.profile.ProfileFragment;
import com.example.quotion.ui.task.Task;
import com.example.quotion.ui.task.TaskRepository;
import com.example.quotion.ui.task.TaskViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private TaskViewModel taskViewModel;
    private String userId;
    private Fragment homeFragment;
    private Fragment calendarFragment;
    private Fragment focusFragment;
    private Fragment profileFragment;

    private Fragment activeFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Khởi tạo ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        findViewById(R.id.fab_add_task).setOnClickListener(v -> showAddTaskDialog());
        fragmentManager = getSupportFragmentManager();

        // Tạo các fragment một lần
        homeFragment = new HomeFragment();
        calendarFragment = new CalendarFragment();
        focusFragment = new FocusFragment();
        profileFragment = new ProfileFragment();

        // Thêm các fragment vào container và ẩn trừ fragment đầu tiên
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, profileFragment, "4").hide(profileFragment)
                .add(R.id.fragment_container, focusFragment, "3").hide(focusFragment)
                .add(R.id.fragment_container, calendarFragment, "2").hide(calendarFragment)
                .add(R.id.fragment_container, homeFragment, "1") // hiện mặc định
                .commit();

        activeFragment = homeFragment;

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                switchFragment(homeFragment);
                return true;
            } else if (itemId == R.id.navigation_calendar) {
                switchFragment(calendarFragment);
                return true;
            } else if (itemId == R.id.navigation_focus) {
                switchFragment(focusFragment);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                switchFragment(profileFragment);
                return true;
            }

            return false;
        });

        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);

        homeFragment.setArguments(bundle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "task_reminder_channel",
                    "Task Reminder",
                    NotificationManager.IMPORTANCE_HIGH); // IMPORTANCE_HIGH để có tiếng chuông
            channel.setDescription("Channel for task reminders");

            NotificationManager manager = this.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }



    }

    private void switchFragment(Fragment targetFragment) {
        if (targetFragment == activeFragment) return;

        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(targetFragment)
                .commit();

        activeFragment = targetFragment;
    }

    private void showAddTaskDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        EditText edtTitle = view.findViewById(R.id.edtTitle);
        EditText edtDescription = view.findViewById(R.id.edtDescription);
        ImageView btnSelectTime = view.findViewById(R.id.btnSelectTime);
        Spinner spinnerColor = view.findViewById(R.id.spinnerColor);
        ImageView btnSelectCategory = view.findViewById(R.id.btnSelectCategory);  // view là root view nếu trong Dialog
        Button btnSend = view.findViewById(R.id.btnSend);

        String[] colors = { "1", "2", "3","4","5"};

        ColorSpinnerAdapter adapter = new ColorSpinnerAdapter(this, colors);
        spinnerColor.setAdapter(adapter);



        final Calendar selectedDateTime = Calendar.getInstance();
        final String[] selectedTime = {""};

        btnSelectTime.setOnClickListener(v -> {
            showDateTimePicker(dateTime -> {
                selectedTime[0] = dateTime;
                Calendar cal = parseStringToCalendar(dateTime);
                if (cal != null) {
                    selectedDateTime.setTime(cal.getTime()); // cập nhật selectedDateTime với thời gian mới
                }
                Toast.makeText(this, "Date and time selected: " + dateTime, Toast.LENGTH_SHORT).show();
            });
        });



        final String[] selectedCategory = {"Free"}; // mặc định là "Free"
        btnSelectCategory.setOnClickListener(v -> {
            final String[] categories = {"Free", "Busy"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Category");
            builder.setItems(categories, (dialogInterface, which) -> {
                selectedCategory[0] = categories[which];
                Toast.makeText(this, "Selected: " + selectedCategory[0], Toast.LENGTH_SHORT).show();
            });
            builder.show();
        });



        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        btnSend.setOnClickListener(v -> {
            String title = edtTitle.getText().toString();
            String desc = edtDescription.getText().toString();
            String time = selectedTime[0];
            String color = spinnerColor.getSelectedItem().toString();
            String category = selectedCategory[0];

            if (title.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please enter title and select time", Toast.LENGTH_SHORT).show();
                return;
            }
            Calendar now = Calendar.getInstance();
            if (selectedDateTime.before(now)) {
                Toast.makeText(this, "Time must not be in the past", Toast.LENGTH_SHORT).show();
                return;
            }

            Task task = new Task(title, desc, time, color, userId,category);

            // Gọi ViewModel để tạo task
            taskViewModel.createTask(task, new TaskRepository.TaskCallback() {
                @Override
                public void onSuccess() {
                    scheduleTaskAlarm(task);
                    Toast.makeText(MainActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(MainActivity.this, "Failed: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private Calendar parseStringToCalendar(String dateTimeStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date = sdf.parse(dateTimeStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void showDateTimePicker(Consumer<String> onDateTimeSelected) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_date_time_picker, null);
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        Button btnOk = dialogView.findViewById(R.id.btnOk);
        datePicker.setMinDate(System.currentTimeMillis());


        timePicker.setIs24HourView(true);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnOk.setOnClickListener(v -> {
            int year = datePicker.getYear();
            int month = datePicker.getMonth();
            int day = datePicker.getDayOfMonth();

            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day, hour, minute, 0);
            cal.set(Calendar.MILLISECOND, 0);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String dateTime = sdf.format(cal.getTime());

            onDateTimeSelected.accept(dateTime);
            dialog.dismiss();
        });

        dialog.show();
    }

    public void navigateToProfile() {
        BottomNavigationView navView = findViewById(R.id.bottom_nav);
        if (navView != null) {
            navView.setSelectedItemId(R.id.navigation_profile);
        } else {
            Log.e("MainActivity", "bottomNavigationView not found!");
        }
    }

    private void scheduleTaskAlarm(Task task) {
        Intent intent = new Intent(this, TaskAlarmReceiver.class);
        intent.putExtra("title", task.title);
        intent.putExtra("description", task.description);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                task.key != null ? task.key.hashCode() : task.title.hashCode(), // unique ID
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date = sdf.parse(task.startTime); // định dạng thời gian giống như khi tạo task

            if (date != null) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    // mở thẳng đến focus fragment
    private void handleIntent(Intent intent) {
        if (intent != null) {
            String openFragment = intent.getStringExtra("open_fragment");
            if ("focus".equals(openFragment)) {
                switchFragment(focusFragment);
                // Đồng thời đổi selection ở BottomNavigationView cho đúng
                BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
                bottomNav.setSelectedItemId(R.id.navigation_focus);
            }
        }

    }
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "task_reminder_channel";
            String channelName = "Task Reminder Notifications";
            NotificationManager manager = context.getSystemService(NotificationManager.class);

            if (manager != null) {
                NotificationChannel existingChannel = manager.getNotificationChannel(channelId);
                if (existingChannel == null) {
                    NotificationChannel channel = new NotificationChannel(
                            channelId,
                            channelName,
                            NotificationManager.IMPORTANCE_HIGH
                    );
                    channel.setDescription("Notifications for task reminders");
                    // Thiết lập âm thanh mặc định
                    channel.setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI, null);
                    channel.enableVibration(true);

                    manager.createNotificationChannel(channel);
                }
            }
        }
    }

}
