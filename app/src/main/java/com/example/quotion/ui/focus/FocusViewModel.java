package com.example.quotion.ui.focus;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;

public class FocusViewModel extends ViewModel {
    private final FocusRepository repository;
    private final MutableLiveData<Map<String, Long>> _dailyFocusDurations = new MutableLiveData<>();
    public LiveData<Map<String, Long>> dailyFocusDurations = _dailyFocusDurations;

    private final MutableLiveData<Boolean> _saveSuccess = new MutableLiveData<>();
    public LiveData<Boolean> saveSuccess = _saveSuccess;

    public FocusViewModel(){
        repository = new FocusRepository();
    }

    public void saveFocusSession(FocusSession session){
        repository.saveFocusSession(session, success -> _saveSuccess.postValue(success));
    }
    public void loadFocusSessionsForChart(){
        repository.loadFocusSessionsPerDay(result -> _dailyFocusDurations.postValue(result));
    }
}
