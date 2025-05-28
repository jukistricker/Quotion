package com.example.quotion.ui.task;

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
}

