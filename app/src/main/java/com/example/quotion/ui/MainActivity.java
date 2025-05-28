package com.example.quotion.ui;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

        String[] colors = {
                "Red", "Orange", "Yellow", "Lime", "Green",
                "Cyan", "Sky Blue", "Blue", "Indigo", "Violet"
        };

        ColorSpinnerAdapter adapter = new ColorSpinnerAdapter(this, colors);
        spinnerColor.setAdapter(adapter);



        final Calendar selectedDateTime = Calendar.getInstance();
        final String[] selectedTime = {""};

        btnSelectTime.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            new TimePickerDialog(this, (timePicker, hour, minute) -> {
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hour);
                selectedDateTime.set(Calendar.MINUTE, minute);
                selectedDateTime.set(Calendar.SECOND, 0);
                selectedDateTime.set(Calendar.MILLISECOND, 0);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                selectedTime[0] = sdf.format(selectedDateTime.getTime());

                Toast.makeText(this, "Time selected: " + selectedTime[0], Toast.LENGTH_SHORT).show();
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show();
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

            Task task = new Task(title, desc, time, color, userId,category);

            // Gọi ViewModel để tạo task
            taskViewModel.createTask(task, new TaskRepository.TaskCallback() {
                @Override
                public void onSuccess() {
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


}
