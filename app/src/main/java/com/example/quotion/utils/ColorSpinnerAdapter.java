package com.example.quotion.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.quotion.R;

import java.util.HashMap;
import java.util.Map;

public class ColorSpinnerAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] colors;
    public static final Map<String, Integer> colorMap= new HashMap<>();

    static {
        colorMap.put("1", Color.parseColor("#87CEEB"));
        colorMap.put("2", Color.parseColor("#59F48D"));
        colorMap.put("3", Color.parseColor("#FFEE80"));
        colorMap.put("4", Color.parseColor("#FFCC80"));
        colorMap.put("5", Color.parseColor("#FF8080"));
    }
    public ColorSpinnerAdapter(Context context, String[] colors) {
        super(context, R.layout.item_color_spinner, colors);
        this.context = context;
        this.colors = colors;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return createColorItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return createColorItemView(position, convertView, parent);
    }

    private View createColorItemView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_color_spinner, parent, false);

        TextView colorName = view.findViewById(R.id.color_name);
        View colorCircle = view.findViewById(R.id.color_circle);

        String colorText = colors[position];
        colorName.setText(colorText);

        Integer color = colorMap.get(colorText);
        if (color != null) {
            GradientDrawable bgShape = (GradientDrawable) context.getDrawable(R.drawable.circle_color_shape).mutate();
            bgShape.setColor(color);
            colorCircle.setBackground(bgShape);
        }

        return view;
    }
}

