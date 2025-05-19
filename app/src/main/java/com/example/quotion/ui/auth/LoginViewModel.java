package com.example.quotion.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginViewModel  {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final MutableLiveData<String> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Void> registerClicked = new MutableLiveData<>();
    private final AuthRepository authRepository;
    private final MutableLiveData<Boolean> isLoginEnabled = new MutableLiveData<>(false);
    public LoginViewModel(Activity activity) {
        authRepository = new AuthRepository(activity);
    }


    public LiveData<Void> getRegisterClicked() {
        return registerClicked;
    }
    public void onRegisterClicked() {
        registerClicked.setValue(null); // Gửi sự kiện cho View
    }
    public LiveData<String> getLoginResult() {
        return loginResult;
    }
    public LiveData<Boolean> getIsLoginEnabled() {
        return isLoginEnabled;
    }

    public void onInputChanged(String email, String password) {
        boolean isValidEmail = email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
        boolean isValidPassword = password != null && password.length() >= 8;

        isLoginEnabled.setValue(isValidEmail && isValidPassword);
    }
    public Task<GoogleSignInAccount> handleGoogleSignInResult(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result != null && result.isSuccess()) {
            return Tasks.forResult(result.getSignInAccount());
        } else {
            return Tasks.forException(new Exception("Google Sign-In failed"));
        }
    }

    public Task<AuthResult> signInWithGoogleAccount(String idToken) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        return auth.signInWithCredential(credential);
    }
    public Intent getGoogleSignInIntent() {
        return authRepository.getGoogleSignInClient().getSignInIntent();
    }

    public Task<AuthResult> signInWithGoogleAccount(GoogleSignInAccount account) {
        // Lấy IdToken từ GoogleSignInAccount
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        // Đăng nhập vào Firebase với credential này
        return authRepository.signInWithCredential(credential);
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

