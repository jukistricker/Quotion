package com.example.quotion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quotion.ui.MainActivity;
import com.example.quotion.ui.intro.IntroActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra SharedPreferences
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isFirstTime = prefs.getBoolean("isFirstTime", true);

        if (isFirstTime) {
            // Chuyển sang IntroActivity nếu là lần đầu
            startActivity(new Intent(this, IntroActivity.class));

            // Cập nhật trạng thái để lần sau không chạy Intro nữa
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isFirstTime", false);
            editor.apply();
        } else {
            // Nếu không phải lần đầu, chuyển thẳng đến MainActivity
            startActivity(new Intent(this, MainActivity.class));
        }

        finish(); // Đóng SplashActivity
    }
}
