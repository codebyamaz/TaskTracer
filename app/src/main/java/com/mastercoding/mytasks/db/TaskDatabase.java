package com.mastercoding.mytasks.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.mastercoding.mytasks.db.entity.Task;

@Database(entities = {Task.class}, version = 1)
public abstract class TaskDatabase extends RoomDatabase {
    public abstract TaskDAO getTaskDAO();
}
