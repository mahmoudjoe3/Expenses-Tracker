package com.mahmoudjoe3.tracker.repo;

import android.content.Context;

import com.mahmoudjoe3.tracker.database.AppDataBase;


public class Repo {

    static Repo instance;
    static AppDataBase dataBase;
    public static Repo getInstance(Context context) {
        if(instance==null){
            instance=new Repo();
            dataBase= AppDataBase.getInstance(context);
        }
        return instance;
    }

    public AppDataBase getDataBase() {
        return dataBase;
    }
}
