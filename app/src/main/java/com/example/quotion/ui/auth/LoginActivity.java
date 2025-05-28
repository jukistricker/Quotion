package com.example.quotion.ui.auth;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.CredentialManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.quotion.BuildConfig;
import com.example.quotion.R;
import com.example.quotion.ui.MainActivity;
import com.example.quotion.ui.intro.LoginorRegister;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnGoogle;
    private ImageView btnBack;
    private com.example.quotion.ui.auth.LoginViewModel loginViewModel;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private CredentialManager credentialManager;
    private static final int RC_SIGN_IN = 9001;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView tvRegister = findViewById(R.id.tv_register);
        String fullText = "Don’t have an account? Register";
        SpannableString spannableString = new SpannableString(fullText);

// Tô màu xám cho phần đầu
        ForegroundColorSpan graySpan = new ForegroundColorSpan(Color.parseColor("#979797"));
        spannableString.setSpan(graySpan, 0, fullText.indexOf("Register"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

// Clickable và tô trắng cho "Register"
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // Mở RegisterActivity khi bấm vào "Register"
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.WHITE);         // Chữ trắng
                ds.setUnderlineText(false);       // Không gạch dưới
            }
        };

        int start = fullText.indexOf("Register");
        int end = start + "Register".length();
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

// Gán vào TextView
        tvRegister.setText(spannableString);
        tvRegister.setMovementMethod(LinkMovementMethod.getInstance());
        tvRegister.setHighlightColor(Color.TRANSPARENT);  // Không có hiệu ứng nền khi bấm


        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();

        etUsername = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnGoogle = findViewById(R.id.btn_google);
//        btnApple = findViewById(R.id.btn_apple);
        btnBack = findViewById(R.id.btn_back);

        // Firebase Auth instance
        mAuth = FirebaseAuth.getInstance();
        String clientId = BuildConfig.DEFAULT_WEB_CLIENT_ID;

// Google Sign-In config
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(clientId) // trong google-services.json
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogle.setOnClickListener(v -> signInWithGoogle());



        // Khởi tạo ViewModel
        loginViewModel = new LoginViewModel(this);

        // Observe LiveData thay đổi trạng thái button
        loginViewModel.getIsLoginEnabled().observe(this, isEnabled -> {
            btnLogin.setEnabled(isEnabled != null && isEnabled); // tránh NPE
        });

        // TextWatcher lắng nghe thay đổi nhập liệu
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginViewModel.onInputChanged(
                        etUsername.getText().toString(),
                        etPassword.getText().toString()
                );
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        etUsername.addTextChangedListener(watcher);
        etPassword.addTextChangedListener(watcher);

        btnLogin.setOnClickListener(v -> {
            String email = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if (!email.isEmpty() && !password.isEmpty()) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                                navigateToMain();
                            } else {
                                Toast.makeText(this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            }
        });

        btnGoogle.setOnClickListener(v -> {
            signInWithGoogle();
            Toast.makeText(this, "Google Login clicked", Toast.LENGTH_SHORT).show();
            // TODO: Handle Google Login
        });

//        btnApple.setOnClickListener(v -> {
//            Toast.makeText(this, "Apple Login clicked", Toast.LENGTH_SHORT).show();
//            // TODO: Handle Apple Login
//        });
        //Xử lý nút trở lại trên trang đăng nhập
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("LoginActivity", "onActivityResult called with requestCode: " + requestCode + ", resultCode: " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            loginViewModel.handleGoogleSignInResult(data)
                    .addOnSuccessListener(account -> {
                        String idToken = account.getIdToken();
                        if (idToken != null) {
                            loginViewModel.signInWithGoogleAccount(idToken)
                                    .addOnSuccessListener(authResult -> {
                                        Log.d("LoginActivity", "GoogleSignInAccount success");

                                        Toast.makeText(this, "Signed in as: " + authResult.getUser().getEmail(), Toast.LENGTH_SHORT).show();
                                        navigateToMain();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("LoginActivity", "Google sign-in failed", e);
                                        Toast.makeText(this, "Firebase login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(this, "ID token is null", Toast.LENGTH_SHORT).show();
                            Log.e("Juki Error", "Id null");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("LoginActivity", "Google sign-in failed", e);
                        Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Google Sign-In", "Failed", e);
                    });
        }
    }


    // Navigate to MainActivity
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Google Login Success: " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        // TODO: chuyển vào HomeActivity hoặc màn hình chính
                    } else {
                        Toast.makeText(this, "Firebase Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}
