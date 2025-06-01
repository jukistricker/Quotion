package com.example.quotion.ui.intro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quotion.R;
import com.example.quotion.ui.auth.LoginActivity;
import com.example.quotion.ui.auth.RegisterActivity;

public class LoginorRegister extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_login);

        Button btnLogin = findViewById(R.id.btn_login1);
        Button btnRegister = findViewById(R.id.btn_create_account1);
        ImageView btnBack = findViewById(R.id.btn_back1);

        btnLogin.setOnClickListener(view -> Login());
        btnRegister.setOnClickListener(view -> Register());
        btnBack.setOnClickListener(view -> back());


    }
    private void Login() {
        // Xử lý chuyển hướng đến trang đăng nhập
        startActivity(new Intent(LoginorRegister.this, LoginActivity.class));
        finish();

    }
    private void Register() {
        // Xử lý chuyển hướng đến trang đăng ký
        startActivity(new Intent(LoginorRegister.this, RegisterActivity.class));
        finish();
    }
    private void back(){
        startActivity(new Intent(this, IntroNavigationActivity.class));
        finish();
    }
}
