package com.example.quotion.ui.task;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class TaskViewModel extends ViewModel {

    private final TaskRepository repository = new TaskRepository();
    private LiveData<List<Task>> userTasks;

    public LiveData<List<Task>> getTasks(String userId) {
        if (userTasks == null) {
            userTasks = repository.getUserTasks(userId);
        }
        return userTasks;
    }
    public void createTask(Task task, TaskRepository.TaskCallback callback) {
        repository.addTask(task, callback);
    }
    public void updateTask(Context context, Task task) {
        repository.updateTask(task, new TaskRepository.TaskCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "Task updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                Log.e("TaskViewModel", "Update failed: " + message);
                Toast.makeText(context, "Update failed: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteTask(Context context, Task task) {
        repository.deleteTask(task, new TaskRepository.TaskCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "Task removed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                Log.e("TaskViewModel", "Remove failed: " + message);
                Toast.makeText(context, "Remove failed: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }



}

