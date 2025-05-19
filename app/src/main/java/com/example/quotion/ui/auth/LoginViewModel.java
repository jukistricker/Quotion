package com.example.quotion.ui.auth;

import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

public class LoginViewModel extends ViewModel {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final MutableLiveData<String> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Void> registerClicked = new MutableLiveData<>();

    public LiveData<Void> getRegisterClicked() {
        return registerClicked;
    }
    public void onRegisterClicked() {
        registerClicked.setValue(null); // Gửi sự kiện cho View
    }
    public LiveData<String> getLoginResult() {
        return loginResult;
    }

    public Task<GoogleSignInAccount> handleGoogleSignInResult(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result != null && result.isSuccess()) {
            return Tasks.forResult(result.getSignInAccount());
        } else {
            return Tasks.forException(new Exception("Google Sign-In failed"));
        }
    }
    public void login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            loginResult.setValue("No empty field allowed");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        loginResult.setValue(userId);

                    } else {
                        loginResult.setValue("Login Failed");
                    }
                });
    }
}

