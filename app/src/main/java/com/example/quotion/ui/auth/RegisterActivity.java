package com.example.quotion.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quotion.R;
import com.example.quotion.ui.MainActivity;
import com.example.quotion.utils.SimpleTextWatcher;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.android.material.button.MaterialButton;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private MaterialButton btnGoogle;
    private static final int RC_SIGN_IN = 100;
    private RegisterViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        viewModel = new RegisterViewModel(this);


        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        btnGoogle = findViewById(R.id.btn_google);

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
