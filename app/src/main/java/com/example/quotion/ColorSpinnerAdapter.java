package com.example.quotion;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ColorSpinnerAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] colors;
    private final Map<String, Integer> colorMap;

    public ColorSpinnerAdapter(Context context, String[] colors) {
        super(context, R.layout.item_color_spinner, colors);
        this.context = context;
        this.colors = colors;

        // Tạo map từ tên sang màu thật (hex)
        colorMap = new HashMap<>();
        colorMap.put("Red", Color.RED);
        colorMap.put("Orange", Color.parseColor("#FFA500"));
        colorMap.put("Yellow", Color.YELLOW);
        colorMap.put("Lime", Color.parseColor("#00FF00"));
        colorMap.put("Green", Color.GREEN);
        colorMap.put("Cyan", Color.CYAN);
        colorMap.put("Sky Blue", Color.parseColor("#87CEEB"));
        colorMap.put("Blue", Color.BLUE);
        colorMap.put("Indigo", Color.parseColor("#4B0082"));
        colorMap.put("Violet", Color.parseColor("#8F00FF"));
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

