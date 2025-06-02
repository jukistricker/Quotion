package com.example.quotion.ui.calendar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quotion.R;
import com.example.quotion.ui.task.Task;
import com.example.quotion.ui.task.TaskAdapter;
import com.example.quotion.ui.task.TaskDetailActivity;
import com.example.quotion.ui.task.TaskViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {
    private CalendarViewModel calendarViewModel;
    private CalendarWidgetView calendarWidget;
    private Handler handler;
    private Runnable updateRunnable;
    private TaskViewModel taskViewModel;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private TextView emptyTextt;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);

        // Inflate layout trước
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Khởi tạo các view bằng root.findViewById
        emptyTextt = root.findViewById(R.id.emptyText);
        recyclerView = root.findViewById(R.id.taskCalendarRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter(task -> {
            Intent intent = new Intent(getContext(), TaskDetailActivity.class);
            intent.putExtra("title", task.title);
            intent.putExtra("description", task.description);
            intent.putExtra("startTime", task.startTime);
            intent.putExtra("color", task.color);
            intent.putExtra("category", task.category);
            intent.putExtra("key", task.key);
            intent.putExtra("userId",task.userId);
            startActivity(intent);
        });
        recyclerView.setAdapter(taskAdapter);

        calendarWidget = root.findViewById(R.id.calendarWidget);

        // Optional: Customize colors
        calendarWidget.setPrimaryColor(Color.parseColor("#FF6B35"));
        calendarWidget.setSecondaryColor(Color.parseColor("#004E89"));
        calendarWidget.setTertiaryColor(Color.parseColor("#1A1A2E"));
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        setupAutoRefresh();

        calendarWidget.setOnDayClickListener(new CalendarWidgetView.OnDayClickListener() {
            @Override
            public void onDayClick(Calendar date) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String selectedDate = sdf.format(date.getTime());

                fetchTasksForDate(selectedDate);
            }
        });



        return root;
    }



    private void setupAutoRefresh() {
        handler = new Handler();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (calendarWidget != null) {
                    calendarWidget.refreshCalendar();
                }
                handler.postDelayed(this, 60000); // every minute
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        if (calendarWidget != null) {
            calendarWidget.refreshCalendar();
        }
        if (handler != null) {
            handler.post(updateRunnable);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }
    private void fetchTasksForDate(String selectedDate) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        FirebaseDatabase.getInstance().getReference("Tasks")
                .orderByChild("userId")
                .equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Task> tasks = new ArrayList<>();

                        for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                            Task task = taskSnapshot.getValue(Task.class);
                            if (task != null && task.getStartTime() != null &&
                                    task.getStartTime().startsWith(selectedDate)) {
                                tasks.add(task);
                            }
                        }

                        Collections.sort(tasks, (t1, t2) -> t1.getStartTime().compareTo(t2.getStartTime()));
                        taskAdapter.setTasks(tasks);

                        // Ẩn/hiện text thông báo

                        emptyTextt.setVisibility(tasks.isEmpty() ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }


    private void renderTasks(List<Task> tasks) {
        Log.d("TASK", "Rendering " + tasks.size() + " tasks");

        for (Task task : tasks) {
            Log.d("TASK", task.getStartTime() + " - " + task.getTitle());
        }

        // Cập nhật adapter (nếu bạn đã setup)
        taskAdapter.setTasks(tasks);
        taskAdapter.notifyDataSetChanged();
    }


}
