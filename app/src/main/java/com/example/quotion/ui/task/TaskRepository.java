package com.example.quotion.ui.task;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskRepository {

    private final DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference("Tasks");

    public interface TaskCallback {
        void onSuccess();
        void onFailure(String message);
    }

    public void addTask(Task task, TaskCallback callback) {
        taskRef.orderByChild("userId").equalTo(task.getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            String time = snap.child("startTime").getValue(String.class);
                            if (task.getStartTime().equals(time)) {
                                callback.onFailure("Task at this time already exists.");
                                return;
                            }
                        }

                        // 🔧 Tạo key trước, rồi set vào Task object
                        String key = taskRef.push().getKey();
                        if (key != null) {
                            task.setKey(key);
                            taskRef.child(key).setValue(task)
                                    .addOnSuccessListener(unused -> callback.onSuccess())
                                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                        } else {
                            callback.onFailure("Failed to generate Firebase key.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure("Firebase error: " + error.getMessage());
                    }
                });
    }


    public LiveData<List<Task>> getUserTasks(String userId) {
        MutableLiveData<List<Task>> taskList = new MutableLiveData<>();
        taskRef.orderByChild("userId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Task> list = new ArrayList<>();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Task task = snap.getValue(Task.class);
                            if (task != null) {
                                task.setKey(snap.getKey());
                                list.add(task);
                            }
                        }
                        taskList.setValue(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        taskList.setValue(null);
                    }
                });

        return taskList;
    }
    public void updateTask(Task task, TaskCallback callback) {
        String key = task.getKey();
        if (key == null || key.isEmpty()) {
            callback.onFailure("Task key is missing");
            return;
        }

        taskRef.child(key).setValue(task)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void deleteTask(Task task, TaskCallback callback) {
        String key = task.getKey();
        if (key == null || key.isEmpty()) {
            callback.onFailure("Task key is missing");
            return;
        }

        taskRef.child(key).removeValue()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }


}

