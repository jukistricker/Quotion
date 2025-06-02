package com.example.quotion.ui.task;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.quotion.R;
import com.example.quotion.ui.MainActivity;
import com.example.quotion.ui.category.CategorySpinnerAdapter;
import com.example.quotion.ui.priority.PrioritySpinnerAdapter;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvDescription, timeValue, priorityValue;
//    private LinearLayout categoryContainer;
    private Task currentTask;
    private TaskViewModel taskViewModel;
    private String currentTaskKey;
    private Spinner spinnerCategory, spinnerColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerColor = findViewById(R.id.spinnerColoredt);



        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        timeValue = findViewById(R.id.time_value);
//        categoryContainer = findViewById(R.id.category_value_container);

        timeValue.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, selectedHour, selectedMinute) -> {
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        selectedCalendar.set(Calendar.MINUTE, selectedMinute);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                        String newTime = sdf.format(selectedCalendar.getTime());

                        timeValue.setText(newTime);
                        currentTask.setStartTime(newTime);
                    }, hour, minute, true);

            timePickerDialog.show();
        });


        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        currentTaskKey = intent.getStringExtra("key");
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String startTime = intent.getStringExtra("startTime");
        String color = intent.getStringExtra("color");
        String category = intent.getStringExtra("category");
        String userId = intent.getStringExtra("userId");

        currentTask = new Task(title, description, startTime, color, userId, category);
        currentTask.setKey(currentTaskKey);

        tvTitle.setText(title);
        tvDescription.setText(description);
        timeValue.setText(startTime);

        List<String> categoryList = Arrays.asList("Free", "Busy");
        List<String> colorList = Arrays.asList("1", "2", "3","4","5");

        CategorySpinnerAdapter categoryAdapter = new CategorySpinnerAdapter(this, categoryList);
        PrioritySpinnerAdapter priorityAdapter = new PrioritySpinnerAdapter(this, colorList);
        spinnerCategory.setAdapter(categoryAdapter);
        spinnerColor.setAdapter(priorityAdapter);

//        TextView categoryText = (TextView) categoryContainer.getChildAt(0);
//        categoryText.setText(category);

        ImageButton btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(v -> showEditDialog());

        // Set lại spinner đúng vị trí
        int categoryPosition = categoryList.indexOf(category);
        if (categoryPosition >= 0) spinnerCategory.setSelection(categoryPosition);

        int colorPosition = colorList.indexOf(getColorNameFromHex(color));
        if (colorPosition >= 0) spinnerColor.setSelection(colorPosition);

        // Set màu ban đầu cho category container
        try {
            int parsedColor = Color.parseColor(color);
            GradientDrawable bg = new GradientDrawable();
            bg.setColor(parsedColor);
            bg.setCornerRadius(8);
//            categoryContainer.setBackground(bg);
        } catch (Exception e) {
//            categoryContainer.setBackgroundColor(Color.GRAY);
            Log.e("TaskDetailActivity" , e.getMessage());
        }

        Button saveButton = findViewById(R.id.btn_edit_task);
        saveButton.setOnClickListener(v -> {
            String selectedCategory = spinnerCategory.getSelectedItem().toString();
            String selectedColorName = spinnerColor.getSelectedItem().toString();
            String selectedColorHex = getColorHex(selectedColorName);

            currentTask.setCategory(selectedCategory);
            currentTask.setColor(selectedColorHex);

            try {

                taskViewModel.updateTask(this,currentTask);


                // Đổi màu nền category ngay
                GradientDrawable bg = new GradientDrawable();
                bg.setColor(Color.parseColor(selectedColorHex));
                bg.setCornerRadius(8);
//                categoryContainer.setBackground(bg);
                navigateToHome();
            } catch (Exception ex) {
                Toast.makeText(this, "Task failed", Toast.LENGTH_SHORT).show();
                Log.e("TaskDetailActivity", ex.getMessage());
            }
        });

        LinearLayout deleteTaskContainer = findViewById(R.id.delete_task_container);
        deleteTaskContainer.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Gọi hàm xóa task ở đây
                        taskViewModel.deleteTask(this,currentTask);
                        navigateToHome();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        FrameLayout btnCancell = findViewById(R.id.btnCancel);
        btnCancell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng activity hiện tại
            }
        });


    }

    private void showEditDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_task, null);
        EditText editTitle = dialogView.findViewById(R.id.editTitle);
        EditText editDescription = dialogView.findViewById(R.id.editDescription);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnConfirmEdit = dialogView.findViewById(R.id.btnConfirmEdit);

        // Gán dữ liệu hiện tại
        editTitle.setText(currentTask.getTitle());
        editDescription.setText(currentTask.getDescription());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirmEdit.setOnClickListener(v -> {
            String newTitle = editTitle.getText().toString().trim();
            String newDesc = editDescription.getText().toString().trim();

            if (newTitle.isEmpty()) {
                Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Cập nhật vào currentTask, nhưng CHƯA update vào ViewModel
            currentTask.setTitle(newTitle);
            currentTask.setDescription(newDesc);

            // ✅ Cập nhật hiển thị UI ngay
            tvTitle.setText(newTitle);
            tvDescription.setText(newDesc);

            dialog.dismiss();
        });

        dialog.show();
    }


    private String getColorNameFromHex(String hex) {
        switch (hex.toLowerCase()) {
            case "#87CEEB": return "1";
            case "#59F48D": return "2";
            case "#FFEE80": return "3";
            case "#FFCC80": return "4";
            case "#FF8080": return "5";
            default: return "1";
        }
    }

    private String getColorHex(String colorName) {
        switch (colorName.toLowerCase()) {
            case "1": return "#87CEEB";
            case "2": return "#59F48D";
            case "3": return "#FFEE80";
            case "4": return "#FFCC80";
            case "5": return "#FF8080";
            default: return "#87CEEB";
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
