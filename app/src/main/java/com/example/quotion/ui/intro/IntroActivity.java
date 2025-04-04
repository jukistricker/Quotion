package com.example.quotion.ui.intro;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quotion.R;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Ánh xạ các thành phần giao diện
        ImageView logo = findViewById(R.id.logo);
        TextView appName = findViewById(R.id.appName);

        // Đặt nội dung động (nếu cần)
        appName.setText("Quotion");
        new Handler().postDelayed(() -> {
            startActivity(new Intent(IntroActivity.this, OnboardingActivity.class));
            finish();
        }, 3000);

    }
}
