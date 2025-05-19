package com.example.quotion.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.quotion.R;
import com.example.quotion.ui.calendar.CalendarFragment;
import com.example.quotion.ui.focus.FocusFragment;
import com.example.quotion.ui.home.HomeFragment;
import com.example.quotion.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    private Fragment homeFragment;
    private Fragment calendarFragment;
    private Fragment focusFragment;
    private Fragment profileFragment;

    private Fragment activeFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Thọ ăn cứt!")
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Ghi dữ liệu thành công"))
                .addOnFailureListener(e -> Log.e("Firebase", "Lỗi ghi dữ liệu", e));

        fragmentManager = getSupportFragmentManager();

        // Tạo các fragment một lần
        homeFragment = new HomeFragment();
        calendarFragment = new CalendarFragment();
        focusFragment = new FocusFragment();
        profileFragment = new ProfileFragment();

        // Thêm các fragment vào container và ẩn trừ fragment đầu tiên
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, profileFragment, "4").hide(profileFragment)
                .add(R.id.fragment_container, focusFragment, "3").hide(focusFragment)
                .add(R.id.fragment_container, calendarFragment, "2").hide(calendarFragment)
                .add(R.id.fragment_container, homeFragment, "1") // hiện mặc định
                .commit();

        activeFragment = homeFragment;

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                switchFragment(homeFragment);
                return true;
            } else if (itemId == R.id.navigation_calendar) {
                switchFragment(calendarFragment);
                return true;
            } else if (itemId == R.id.navigation_focus) {
                switchFragment(focusFragment);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                switchFragment(profileFragment);
                return true;
            }

            return false;
        });

    }

    private void switchFragment(Fragment targetFragment) {
        if (targetFragment == activeFragment) return;

        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(targetFragment)
                .commit();

        activeFragment = targetFragment;
    }
}
