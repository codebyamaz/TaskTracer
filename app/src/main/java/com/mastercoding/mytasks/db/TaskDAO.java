package com.mastercoding.mytasks.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mastercoding.mytasks.db.entity.Task;

import java.util.List;
@Dao
public interface TaskDAO {
    @Insert
    long addTask(Task task);
    @Insert
    List<Long> addTasks(List<Task> tasks);
    @Update
    void updateTask(Task task);
    @Delete
    void deleteTask(Task task);
    @Query("SELECT * FROM tasks")
    List<Task> getTasks();
    @Query("SELECT * FROM tasks WHERE task_id = :taskId")
    Task getTask(long taskId);
}
