package com.example.quotion.ui.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.quotion.R;

import java.util.List;

public class CategorySpinnerAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> categories;

    public CategorySpinnerAdapter(Context context, List<String> categories) {
        super(context, R.layout.item_category_spinner, categories);
        this.context = context;
        this.categories = categories;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_spinner, parent, false);
        TextView textView = view.findViewById(R.id.category_name);
        textView.setText(categories.get(position));
        return view;
    }
}


