package com.example.quotion.ui.auth;

import android.app.Activity;
import android.content.Intent;

import com.example.quotion.ui.auth.*;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;

public class RegisterViewModel {
    private final AuthRepository authRepository;

    public RegisterViewModel(Activity activity) {
        authRepository = new AuthRepository(activity);
    }

    // Validate email
    public boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Check password match
    public boolean doPasswordsMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    // Register with email/password
    public Task<AuthResult> register(String email, String password) {
        return authRepository.registerOrLoginWithEmail(email, password);
    }

    // Get Google Sign-In Intent
    public Intent getGoogleSignInIntent() {
        return authRepository.getGoogleSignInClient().getSignInIntent();
    }

    // Handle Google Sign-In result
    public Task<GoogleSignInAccount> handleGoogleSignInResult(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result != null && result.isSuccess()) {
            return Tasks.forResult(result.getSignInAccount());
        } else {
            return Tasks.forException(new Exception("Google Sign-In failed"));
        }
    }

    // Firebase sign-in using Google account
    public Task<AuthResult> signInWithGoogleAccount(GoogleSignInAccount account) {
        // Lấy IdToken từ GoogleSignInAccount
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        // Đăng nhập vào Firebase với credential này
        return authRepository.signInWithCredential(credential);
    }
}
