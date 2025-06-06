package com.example.quotion.ui.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quotion.R;
import com.example.quotion.ui.MainActivity;
import com.example.quotion.ui.intro.IntroNavigationActivity;
import com.example.quotion.ui.intro.LoginorRegister;
import com.example.quotion.utils.SimpleTextWatcher;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private ImageView btnBack;
    private MaterialButton btnGoogle;
    private static final int RC_SIGN_IN = 100;
    private RegisterViewModel viewModel;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User đã đăng nhập rồi => chuyển thẳng sang Main
            navigateToMain();
        }
    }


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView tvRegister = findViewById(R.id.txt_already_have_account);
        String fullText = "Already have an account? Login";
        SpannableString spannableString = new SpannableString(fullText);

// Tô màu xám cho phần đầu
        ForegroundColorSpan graySpan = new ForegroundColorSpan(Color.parseColor("#979797"));
        spannableString.setSpan(graySpan, 0, fullText.indexOf("Login"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

// Clickable và tô trắng cho "Login"
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // Mở RegisterActivity khi bấm vào "Register"
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.WHITE);         // Chữ trắng
                ds.setUnderlineText(false);       // Không gạch dưới
            }
        };

        int start = fullText.indexOf("Login");
        int end = start + "Login".length();
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

// Gán vào TextView
        tvRegister.setText(spannableString);
        tvRegister.setMovementMethod(LinkMovementMethod.getInstance());
        tvRegister.setHighlightColor(Color.TRANSPARENT);  // Không có hiệu ứng nền khi bấm


        viewModel = new RegisterViewModel(this);


        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        btnGoogle = findViewById(R.id.btn_google);
        btnBack = findViewById(R.id.btn_back);

        btnRegister.setEnabled(false);

        // TextWatcher for inputs validation
        TextWatcher watcher = new SimpleTextWatcher(this::checkInputs);
        edtEmail.addTextChangedListener(watcher);
        edtPassword.addTextChangedListener(watcher);
        edtConfirmPassword.addTextChangedListener(watcher);

        // Register button click
        btnRegister.setOnClickListener(v -> register());

        // Google Sign-In button click
        btnGoogle.setOnClickListener(v -> {
            Intent signInIntent = viewModel.getGoogleSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
        // Back button click
        btnBack.setOnClickListener(v -> back());
    }

    // Check if all fields are valid
    private void checkInputs() {
        boolean isValid = !edtEmail.getText().toString().isEmpty()
                && !edtPassword.getText().toString().isEmpty()
                && !edtConfirmPassword.getText().toString().isEmpty();
        btnRegister.setEnabled(isValid);
    }

    // Handle registration logic
    private void register() {
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();

        if (!viewModel.isValidEmail(email)) {
            edtEmail.setError("Invalid email");
            return;
        }
        if (!viewModel.doPasswordsMatch(password, confirmPassword)) {
            edtConfirmPassword.setError("Passwords do not match");
            return;
        }

        viewModel.register(email, password)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Registered/Login successfully", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                })
                .addOnFailureListener(e -> {
                    handleError(e);
                });
    }

    // Navigate to MainActivity
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Handle errors during registration/login
    private void handleError(Exception e) {
        if (e instanceof FirebaseAuthException) {
            Toast.makeText(this, "Error: " + ((FirebaseAuthException) e).getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Registration/Login failed", Toast.LENGTH_SHORT).show();
        }
    }
    //xử lý nút bấm quay trở lại
    private void back(){
        startActivity(new Intent(this, IntroNavigationActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            viewModel.handleGoogleSignInResult(data)
                    .addOnSuccessListener(account -> {
                        viewModel.signInWithGoogleAccount(account)
                                .addOnSuccessListener(authResult -> {
                                    Toast.makeText(this, "Signed in as: " + authResult.getUser().getEmail(), Toast.LENGTH_SHORT).show();
                                    navigateToMain();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Firebase login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        if (e instanceof ApiException) {
                            Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }
}
