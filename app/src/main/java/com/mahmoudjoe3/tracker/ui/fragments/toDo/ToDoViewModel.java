package com.mahmoudjoe3.tracker.ui.fragments.toDo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.mahmoudjoe3.tracker.pojo.TodoNote;
import com.mahmoudjoe3.tracker.repo.Repo;

import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ToDoViewModel extends AndroidViewModel {

    private Repo repo;
    public ToDoViewModel(@NonNull Application application) {
        super(application);
        repo=Repo.getInstance(application);
    }


    public void insert(TodoNote note,OnCompleteListener completeListener){
        setOnCompleteListener(completeListener);

        repo.getDataBase().getDao().insertToDo(note)
                .subscribeOn(Schedulers.computation())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onComplete() {
                        onCompleteListener.onComplete(note);
                    }
                    @Override
                    public void onError(Throwable e) { }
                });
    }
    public void delete(TodoNote note,OnCompleteListener completeListener){
        setOnCompleteListener(completeListener);

        repo.getDataBase().getDao().deleteToDo(note)
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onComplete() {
                        onCompleteListener.onComplete(note);
                    }
                    @Override
                    public void onError(Throwable e) { }
                });
    }
    public void update(TodoNote note,OnCompleteListener completeListener){
        setOnCompleteListener(completeListener);

        repo.getDataBase().getDao().updateToDo(note)
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onComplete() {
                        onCompleteListener.onComplete(note);
                    }
                    @Override
                    public void onError(Throwable e) { }
                });
    }
    public Single<List<TodoNote>> getData() {
        return repo.getDataBase().getDao().getToDoList();
    }
    public Single<TodoNote> getNote(int id) {
        return repo.getDataBase().getDao().getToDoNote(id);
    }

    private void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }
    OnCompleteListener onCompleteListener;
    public interface OnCompleteListener{
        void onComplete(TodoNote note);
    }


}
