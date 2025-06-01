package com.example.quotion.ui.priority;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.quotion.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrioritySpinnerAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> priorities;
    private final Map<String, Integer> priorityColors;

    public PrioritySpinnerAdapter(Context context, List<String> priorities) {
        super(context, R.layout.item_priority_spinner, priorities);
        this.context = context;
        this.priorities = priorities;

        priorityColors = new HashMap<>();
        priorityColors.put("Low", Color.GREEN);
        priorityColors.put("Medium", Color.YELLOW);
        priorityColors.put("High", Color.RED);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_priority_spinner, parent, false);
        TextView textView = view.findViewById(R.id.priority_name);
        View colorCircle = view.findViewById(R.id.priority_color);

        String priority = priorities.get(position);
        textView.setText(priority);
        Integer color = priorityColors.get(priority);
        if (color != null) {
            GradientDrawable bgShape = new GradientDrawable();
            bgShape.setShape(GradientDrawable.OVAL);
            bgShape.setColor(color);
            colorCircle.setBackground(bgShape);
        }

        return view;
    }
}
