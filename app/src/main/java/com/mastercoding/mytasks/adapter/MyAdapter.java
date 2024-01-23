package com.mastercoding.mytasks.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mastercoding.mytasks.MainActivity;
import com.mastercoding.mytasks.R;
import com.mastercoding.mytasks.db.entity.Task;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private final Context context;
    private final List<Task> dataModels;
    private final MainActivity mainActivity;

    public MyAdapter(Context context, List<Task> dataModels, MainActivity mainActivity) {
        this.context = context;
        this.dataModels = dataModels;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.content_itemlist, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Task task = dataModels.get(position);
        holder.taskName.setText(task.getName());
        holder.taskDeadline.setText(task.getDeadline());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.addOrEditTask(true, task, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView taskName;
        private final TextView taskDeadline;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskDeadline = itemView.findViewById(R.id.taskDeadline);
            }
        }
    }
