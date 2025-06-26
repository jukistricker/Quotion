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

    // --- lưu trạng thái timer ---
    private final MutableLiveData<Long> _timeLeftMillis = new MutableLiveData<>();
    public LiveData<Long> timeLeftMillis = _timeLeftMillis;

    private final MutableLiveData<Boolean> _isRunning = new MutableLiveData<>(false);
    public LiveData<Boolean> isRunning = _isRunning;
    // ----------------------------------------------

    public FocusViewModel(){
        repository = new FocusRepository();
    }

    public void saveFocusSession(FocusSession session){
        repository.saveFocusSession(session, success -> _saveSuccess.postValue(success));
    }
    public void loadFocusSessionsForChart(){
        repository.loadFocusSessionsPerDay(result -> _dailyFocusDurations.postValue(result));
    }
    public void setTimeLeftMillis(long millis){
        _timeLeftMillis.postValue(millis);
    }
    public void setIsRunning(boolean running){
        _isRunning.postValue(running);
    }
}
