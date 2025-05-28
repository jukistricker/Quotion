package com.example.quotion.ui.intro;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.example.quotion.R;

public class IntroNavigationActivity extends AppCompatActivity {

    ViewPager slideViewPager;
    LinearLayout dotIndicator;
    IntroNavigationAdapter introNavigationAdapter;
    Button btnNext, btnSkip, btnBack;
    TextView[] dots;
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setDotIndicator(position);
            if (position > 0) {
                btnBack.setEnabled(true);
                btnBack.setVisibility(View.VISIBLE);
            } else {
                btnBack.setEnabled(false);
                btnBack.setVisibility(View.INVISIBLE);
            }
            btnNext.setText(position == 2 ? "Finish" : "Next");


        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro_navigation);

        btnNext = findViewById(R.id.nextButton);
        btnBack = findViewById(R.id.backButton);
        btnSkip = findViewById(R.id.skipButton);

        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (getItem(0) > 0) {
                    slideViewPager.setCurrentItem(getItem(-1), true);
                }
        }

        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (slideViewPager.getCurrentItem() < 2){
                    slideViewPager.setCurrentItem(getItem(1), true);
                }else{
                    Intent intent = new Intent(IntroNavigationActivity.this, LoginorRegister.class );
                    startActivity(intent);
                    finish();
                }
            }
        });
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntroNavigationActivity.this, LoginorRegister.class );
                startActivity(intent);
                finish();
                Toast.makeText(IntroNavigationActivity.this, "Skip", Toast.LENGTH_SHORT).show();
            }
        });
        slideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        dotIndicator = (LinearLayout) findViewById(R.id.dotIndicator);
        introNavigationAdapter = new IntroNavigationAdapter(this);
        slideViewPager.setAdapter(introNavigationAdapter);
        setDotIndicator(0);
        slideViewPager.addOnPageChangeListener(viewListener);
    }
        public void setDotIndicator(int position) {
            dots = new TextView[3];
            dotIndicator.removeAllViews();
            for (int i = 0; i < dots.length; i++) {
                dots[i] = new TextView(this);
                dots[i].setText(Html.fromHtml("&#8226", Html.FROM_HTML_MODE_LEGACY));
                dots[i].setTextSize(35);
                dots[i].setTextColor(getResources().getColor(R.color.gray, getApplicationContext().getTheme()));
                dotIndicator.addView(dots[i]);
            }
            dots[position].setTextColor(getResources().getColor(R.color.hover, getApplicationContext().getTheme()));
        }
        private int getItem(int i) {
            return slideViewPager.getCurrentItem() + i;
        }
}
