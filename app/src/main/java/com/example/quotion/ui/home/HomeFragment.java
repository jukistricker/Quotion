package com.example.quotion.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quotion.R;
import com.example.quotion.ui.MainActivity;
import com.example.quotion.ui.task.Task;
import com.example.quotion.ui.task.TaskAdapter;
import com.example.quotion.ui.task.TaskDetailActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private String currentUserId;
    private ValueEventListener taskListener;
    private List<Task> taskList = new ArrayList<>();  // Danh sách gốc fetch từ Firebase
    private EditText searchEditText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (getArguments() != null) {
            currentUserId = getArguments().getString("userId");
        }

        Log.d("Juki", "currentUserId = " + currentUserId);


        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById(R.id.taskRecyclerView);

        adapter = new TaskAdapter(task -> {
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

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchTasks();

        String photoUrl = null;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null && account.getPhotoUrl() != null) {
            photoUrl = account.getPhotoUrl().toString();
        }

        ShapeableImageView avatar = root.findViewById(R.id.avatar);

        if (photoUrl != null) {
            Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.mipmap.default_avatar_round)
                    .error(R.mipmap.default_avatar_round)
                    .into(avatar);
        }

        avatar.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToProfile();
            }
        });

        searchEditText = root.findViewById(R.id.searchEditText);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTasks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        return root;
    }
    private void filterTasks(String query) {
        if (query == null || query.isEmpty()) {
            adapter.setTasks(taskList); // Hiển thị tất cả nếu query rỗng
            return;
        }

        List<Task> filteredList = new ArrayList<>();
        String lowerCaseQuery = query.toLowerCase();

        for (Task task : taskList) {
            if ((task.getTitle() != null && task.getTitle().toLowerCase().contains(lowerCaseQuery)) ||
                    (task.getDescription() != null && task.getDescription().toLowerCase().contains(lowerCaseQuery))) {
                filteredList.add(task);
            }
        }

        adapter.setTasks(filteredList);
    }

    private void fetchTasks() {
        DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference("Tasks");
        taskListener = taskRef.orderByChild("userId").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        taskList.clear();
                        for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                            Task task = taskSnapshot.getValue(Task.class);

                            if (task != null && isToday(task.getStartTime())) {
                                taskList.add(task);
                            }
                        }
                        adapter.setTasks(taskList);
                        System.out.println(taskList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load tasks.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (taskListener != null) {
            DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference("Tasks");
            taskRef.removeEventListener(taskListener);
        }
    }
    private boolean isToday(String startTime) {
        try {
            // Cắt phần ngày (10 ký tự đầu tiên: yyyy-MM-dd)
            String taskDate = startTime.substring(0, 10);

            // Lấy ngày hôm nay dưới dạng yyyy-MM-dd
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String today = sdf.format(new Date());

            System.out.println("Juki"+ String.valueOf(taskDate.equals(today)));
            return taskDate.equals(today);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

