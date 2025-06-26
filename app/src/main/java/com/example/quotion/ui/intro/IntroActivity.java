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
        setContentView(R.layout.intro1);
        // Chuyển sang màn hình chính sau 3 giây
        new Handler().postDelayed(() -> {
            startActivity(new Intent(IntroActivity.this, IntroNavigationActivity.class));
            finish();
        }, 3000);

    }
}
