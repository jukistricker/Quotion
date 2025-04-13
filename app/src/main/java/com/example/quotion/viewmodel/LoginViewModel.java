package com.example.quotion.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isLoginEnabled = new MutableLiveData<>(false);

    public LiveData<Boolean> getIsLoginEnabled() {
        return isLoginEnabled;
    }

    public void onInputChanged(String username, String password) {
        boolean enabled = !username.isEmpty() && !password.isEmpty();
        isLoginEnabled.setValue(enabled);
    }
}
