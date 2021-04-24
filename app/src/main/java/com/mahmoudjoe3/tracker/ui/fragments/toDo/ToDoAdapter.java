package com.mahmoudjoe3.tracker.ui.fragments.toDo;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mahmoudjoe3.tracker.R;
import com.mahmoudjoe3.tracker.pojo.Expense;
import com.mahmoudjoe3.tracker.pojo.TodoNote;
import com.mahmoudjoe3.tracker.ui.fragments.expenses.ExpensesAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.VH> {

    List<TodoNote> noteList;

    public ToDoAdapter() {
        this.noteList = new ArrayList<>();
    }

    public void setNoteList(List<TodoNote> noteList) {
        if(noteList==null)
            noteList=new ArrayList<>();
        this.noteList = noteList;
        notifyDataSetChanged();
    }
    public void addNote(TodoNote note,int pos) {
        this.noteList.add(pos,note);
        notifyItemInserted(pos);
    }
    public TodoNote removeNote(int pos){
        TodoNote n=noteList.get(pos);
        this.noteList.remove(pos);
        notifyItemRemoved(pos);
        return n;
    }

    public TodoNote getNote(int pos){
        return noteList.get(pos);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        TodoNote todoNote=noteList.get(position);
        holder.note.setText(todoNote.getNote());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        String date = dateFormat.format(new Date(todoNote.getTime()));
        String time = timeFormat.format(new Date(todoNote.getTime()));
        holder.time.setText(time);
        holder.date.setText(date);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onCLick(todoNote);
            }
        });
        if(todoNote.isDone()){
            holder.note.setPaintFlags(holder.note.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }


    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public static class VH extends RecyclerView.ViewHolder{
        TextView note,time,date;
        public VH(@NonNull View itemView) {
            super(itemView);
            note=itemView.findViewById(R.id.it_note);
            time=itemView.findViewById(R.id.it_time);
            date=itemView.findViewById(R.id.it_date);
        }
    }

    OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    interface OnClickListener{
        void onCLick(TodoNote note);
    }
}
