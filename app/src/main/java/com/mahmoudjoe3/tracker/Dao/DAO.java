package com.mahmoudjoe3.tracker.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mahmoudjoe3.tracker.pojo.Expense;
import com.mahmoudjoe3.tracker.pojo.TodoNote;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface DAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertToDo(TodoNote note);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertExpense(Expense expense);

    @Update()
    Completable updateToDo(TodoNote note);
    @Update()
    Completable updateExpense(Expense expense);

    @Delete()
    Completable deleteToDo(TodoNote note);
    @Delete()
    Completable deleteExpense(Expense expense);

    @Query("Select * from TodoNote")
    Single<List<TodoNote>> getToDoList();
    @Query("Select * from Expense")
    Single<List<Expense>> getExpenseList();

    @Query("Select * from TodoNote where TodoNote.id=:id")
    Single<TodoNote> getToDoNote(int id);
    @Query("Select * from Expense where Expense.id=:id")
    Single<Expense> getExpense(int id);



}
