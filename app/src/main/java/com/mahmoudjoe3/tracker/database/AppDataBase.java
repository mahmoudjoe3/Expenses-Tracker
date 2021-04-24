package com.mahmoudjoe3.tracker.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mahmoudjoe3.tracker.Dao.DAO;
import com.mahmoudjoe3.tracker.pojo.Expense;
import com.mahmoudjoe3.tracker.pojo.TodoNote;

@Database(entities = {Expense.class, TodoNote.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {

    public abstract DAO getDao();
    private static AppDataBase instance;

    public static synchronized AppDataBase getInstance(Context context) {
        if(instance ==null){
            instance = Room.databaseBuilder(context.getApplicationContext()
                    ,AppDataBase.class,"database101")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
