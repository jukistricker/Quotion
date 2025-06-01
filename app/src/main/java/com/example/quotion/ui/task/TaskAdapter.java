package com.example.quotion.ui.task;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quotion.R;
import com.example.quotion.utils.ColorSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList = new ArrayList<>();
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public TaskAdapter(){}
    public TaskAdapter(OnTaskClickListener listener) {
        this.listener = listener;
    }

    public void setTasks(List<Task> tasks) {
        this.taskList = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.title.setText(task.title);
        holder.time.setText(task.startTime);
        holder.category.setText(task.category);

        // Tạo GradientDrawable riêng cho từng item
        GradientDrawable bgDrawable = new GradientDrawable();
        bgDrawable.setCornerRadius(8);

        // Tra màu từ colorMap
        Integer bgColor = ColorSpinnerAdapter.colorMap.get(task.color);
        if (bgColor != null) {
            bgDrawable.setColor(bgColor);
        } else {
            try {
                bgDrawable.setColor(Color.parseColor(task.color)); // Nếu là mã màu #hex
            } catch (IllegalArgumentException e) {
                bgDrawable.setColor(Color.GRAY); // fallback nếu sai định dạng
            }
        }

        // Set drawable làm background
        holder.category.setBackground(bgDrawable);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskClick(task);
            }
        });
    }



    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title, time, category;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.meeting_title);
            time = itemView.findViewById(R.id.meeting_time);
            category = itemView.findViewById(R.id.busy_label);
        }
    }
}

