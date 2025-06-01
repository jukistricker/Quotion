package com.example.quotion.ui.intro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.quotion.R;

public class IntroNavigationAdapter extends PagerAdapter {
    Context context;

    int slider_images[] = {R.drawable.intro_2, R.drawable.intro_3, R.drawable.intro_4};
    int slider_headings[] = {R.string.text2_intro2, R.string.text2_intro3, R.string.text2_intro4};
    int slider_desc[] = {R.string.text1_intro2, R.string.text1_intro3, R.string.text1_intro4};


    public IntroNavigationAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount(){
        return slider_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position){
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_screen, container, false);

        //Nạp ảnh vào slider
        ImageView sliderImage = view.findViewById(R.id.sliderImage);
        TextView sliderTitle = view.findViewById(R.id.sliderTitle);
        TextView sliderDesc = view.findViewById(R.id.sliderDesc);

        sliderImage.setImageResource(slider_images[position]);
        sliderTitle.setText(this.slider_headings[position]);
        sliderDesc.setText(this.slider_desc[position]);
        container.addView(view);
        return view;
    }
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object){
        container.removeView((LinearLayout) object);
    }


}
