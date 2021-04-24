package com.mahmoudjoe3.tracker.pojo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TodoNote {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String note;
    private boolean done;
    private long time;

    public TodoNote(String note) {
        this.id = id;
        this.note = note;
        this.done = false;
        this.time=System.currentTimeMillis();
    }

    public TodoNote() {
    }


    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
