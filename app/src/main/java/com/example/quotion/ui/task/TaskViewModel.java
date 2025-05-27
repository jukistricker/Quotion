package com.example.quotion.ui.task;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class TaskViewModel extends ViewModel {

    private final TaskRepository repository = new TaskRepository();

    public LiveData<List<Task>> getTasks(String userId) {
        return repository.getUserTasks(userId);
    }

    public void createTask(Task task, TaskRepository.TaskCallback callback) {
        repository.addTask(task, callback);
    }
}

