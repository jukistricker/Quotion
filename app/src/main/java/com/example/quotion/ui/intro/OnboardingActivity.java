package com.example.quotion.ui.intro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quotion.ui.MainActivity;
import com.example.quotion.R;

public class OnboardingActivity extends AppCompatActivity {

    private TextView tvSkip, tvBack;
    private Button btnNext;
    private ImageView imgLogo;
    private LinearLayout indicatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // Ánh xạ View
        tvSkip = findViewById(R.id.tv_skip);
        tvBack = findViewById(R.id.tv_back);
        btnNext = findViewById(R.id.btn_next);
        imgLogo = findViewById(R.id.img_logo);
        indicatorLayout = findViewById(R.id.indicator_layout);

        // Bắt sự kiện Click
        tvSkip.setOnClickListener(view -> skipOnboarding());
        tvBack.setOnClickListener(view -> goBack());
        btnNext.setOnClickListener(view -> goNext());
    }

    private void skipOnboarding() {
        // Chuyển hướng đến màn hình chính
        startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
        finish();
    }

    private void goBack() {
        // Xử lý khi nhấn "BACK"
        // Có thể làm việc với ViewPager nếu có nhiều trang onboarding
    }

    private void goNext() {
        // Xử lý khi nhấn "NEXT"
        // Có thể thay đổi nội dung hiển thị
    }
}