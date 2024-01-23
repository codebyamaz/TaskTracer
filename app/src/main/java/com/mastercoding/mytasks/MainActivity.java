package com.mastercoding.mytasks;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mastercoding.mytasks.adapter.MyAdapter;
import com.mastercoding.mytasks.db.TaskDatabase;
import com.mastercoding.mytasks.db.entity.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List<Task> dataModels = new ArrayList<>();
    private TaskDatabase taskDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolBar);
        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButton = findViewById(R.id.fab);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Tasks");

        myAdapter = new MyAdapter(this, dataModels, this);
        taskDatabase = Room.databaseBuilder(getApplicationContext(), TaskDatabase.class, "taskDB").allowMainThreadQueries().build();

        initRecyclerView();
        floatingActionButton.setOnClickListener(view -> addOrEditTask(false, null, -1));
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(myAdapter);
        displayTasksInBackground();
    }
    public void addOrEditTask(boolean isUpdated, Task task, int position) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.content_addtask, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(itemView);
        TextView taskTitle = itemView.findViewById(R.id.taskTitle);
        EditText taskName = itemView.findViewById(R.id.taskName);
        EditText taskDeadline = itemView.findViewById(R.id.taskDeadline);
        taskTitle.setText(!isUpdated ? "Add New Task" : "Edit Task");
        if (isUpdated && task != null) {
            taskName.setText(task.getName());
            taskDeadline.setText(task.getDeadline());
        }
        alertDialogBuilder.setCancelable(false).setPositiveButton(isUpdated ? "Update" : "Save", (dialogInterface, i) -> {
        }).setNegativeButton("Delete", (dialogInterface, i) -> {
            if (isUpdated) {
                deleteTask(task, position);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#ADD8E6"));
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#ADD8E6"));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( TextUtils.isEmpty(taskName.getText().toString()) ) {
                    Toast.makeText(MainActivity.this, "Enter Task Name", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }
                if (isUpdated && task != null) {
                    updateTask(taskName.getText().toString(), taskDeadline.getText().toString(), position);
                } else {
                    createTask(taskName.getText().toString(), taskDeadline.getText().toString());
                }
            }
        });
    }
    private void deleteTask(Task task, int position) {
        taskDatabase.getTaskDAO().deleteTask(task);
        dataModels.remove(position);
        myAdapter.notifyItemRemoved(position);
    }

    private void createTask(String name, String deadline) {
        long id = taskDatabase.getTaskDAO().addTask(new Task(name, deadline, 0));
        Task task = taskDatabase.getTaskDAO().getTask(id);

        if (task != null) {
            dataModels.add(0, task);
            myAdapter.notifyItemInserted(0);
        }
    }

    private void updateTask(String name, String deadline, int position) {
        Task task = dataModels.get(position);
        task.setName(name);
        task.setDeadline(deadline);
        taskDatabase.getTaskDAO().updateTask(task);
        dataModels.set(position, task);
        myAdapter.notifyItemChanged(position);
    }

    public void displayTasksInBackground() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            List<Task> tasks = taskDatabase.getTaskDAO().getTasks();
            handler.post(() -> {
                dataModels.clear();
                dataModels.addAll(tasks);
                myAdapter.notifyDataSetChanged();
            });
        });
    }
}
