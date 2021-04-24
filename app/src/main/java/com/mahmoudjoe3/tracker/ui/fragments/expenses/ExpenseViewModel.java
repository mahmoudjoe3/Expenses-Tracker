package com.mahmoudjoe3.tracker.ui.fragments.expenses;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.mahmoudjoe3.tracker.pojo.Expense;
import com.mahmoudjoe3.tracker.repo.Repo;

import java.util.List;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ExpenseViewModel extends AndroidViewModel {
    private Repo repo;

    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        repo=Repo.getInstance(application);
    }

    public void insert(Expense expense, OnCompleteListener completeListener){
        setOnCompleteListener(completeListener);

        repo.getDataBase().getDao().insertExpense(expense)
                .subscribeOn(Schedulers.computation())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onComplete() {
                        onCompleteListener.onComplete(expense);
                    }
                    @Override
                    public void onError(Throwable e) { }
                });
    }
    public void delete(Expense expense, OnCompleteListener completeListener){
        setOnCompleteListener(completeListener);

        repo.getDataBase().getDao().deleteExpense(expense)
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onComplete() {
                        onCompleteListener.onComplete(expense);
                    }
                    @Override
                    public void onError(Throwable e) { }
                });
    }
    public void update(Expense expense, OnCompleteListener completeListener){
        setOnCompleteListener(completeListener);

        repo.getDataBase().getDao().updateExpense(expense)
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onComplete() {
                        onCompleteListener.onComplete(expense);
                    }
                    @Override
                    public void onError(Throwable e) { }
                });
    }
    public Single<List<Expense>> getData() {
       return repo.getDataBase().getDao().getExpenseList();
    }
    public Single<Expense> getExpense(int id) {
        return repo.getDataBase().getDao().getExpense(id);
    }

    private void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }
    public void RemoveOnCompleteListener() {
        this.onCompleteListener = null;
    }
    OnCompleteListener onCompleteListener;
    public interface OnCompleteListener{
        void onComplete(Expense expense);
    }
}
