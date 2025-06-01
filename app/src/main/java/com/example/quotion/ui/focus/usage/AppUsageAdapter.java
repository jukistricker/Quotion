package com.example.quotion.ui.focus.usage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quotion.databinding.ItemAppUsageBinding;
import com.example.quotion.ui.focus.usage.AppUsageItem;

import java.util.List;

public class AppUsageAdapter extends RecyclerView.Adapter<AppUsageAdapter.ViewHolder> {

    private final List<AppUsageItem> usageItems;
    private final Context context;

    public AppUsageAdapter(List<AppUsageItem> usageItems, Context context) {
        this.usageItems = usageItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAppUsageBinding binding = ItemAppUsageBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppUsageItem item = usageItems.get(position);
        holder.binding.imageAppIcon.setImageDrawable(item.getAppIcon());
        holder.binding.textAppName.setText(item.getAppName());
        holder.binding.textUsageTime.setText(item.formatUsageTime(item.getUsageTime()));
    }

    @Override
    public int getItemCount() {
        return usageItems.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemAppUsageBinding binding;

        public ViewHolder(@NonNull ItemAppUsageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
