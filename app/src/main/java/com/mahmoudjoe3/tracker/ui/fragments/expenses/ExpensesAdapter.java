package com.mahmoudjoe3.tracker.ui.fragments.expenses;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mahmoudjoe3.tracker.R;
import com.mahmoudjoe3.tracker.pojo.Expense;

import java.util.ArrayList;
import java.util.List;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.VH> {

    List<Expense> expenseList;
    Context context;
    public ExpensesAdapter(List<Expense> expenseList,Context context1) {
        this.expenseList = expenseList;
        context=context1;
    }

    public void setExpenseList(List<Expense> expenseList) {
        if(expenseList ==null)
            expenseList =new ArrayList<>();
        this.expenseList = expenseList;
        notifyDataSetChanged();
    }
    public void addNote(Expense note,int pos) {
        this.expenseList.add(pos,note);
        notifyItemInserted(pos);
    }
    public Expense removeNote(int pos){
        Expense n= expenseList.get(pos);
        this.expenseList.remove(pos);
        notifyItemRemoved(pos);
        return n;
    }

    @NonNull
    @Override
    public ExpensesAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExpensesAdapter.VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.expenses_item_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ExpensesAdapter.VH holder, int position) {
        Expense expense= expenseList.get(position);
        holder.note.setText(expense.getDescription());
        holder.time.setText(expense.getTime());
        holder.date.setText(expense.getDate());
        holder.cat.setText(expense.getCat());
        holder.cost.setText("$"+(expense.getAmount()*expense.getPrice()));

        GradientDrawable magnitudeCircle = (GradientDrawable) holder.icon.getBackground();
        int megColor= getMagnitudeColorAndSetIcon(getCatPos(expense.getCat()),holder.icon);
        magnitudeCircle.setColor(megColor);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onCLick(expense);
            }
        });
    }
    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class VH extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView cat,note,time,date,cost;
        public VH(@NonNull View itemView) {
            super(itemView);
            note=itemView.findViewById(R.id.eIt_expenses_note);
            time=itemView.findViewById(R.id.eIt_expenses_time);
            date=itemView.findViewById(R.id.eIt_expenses_date);
            cat=itemView.findViewById(R.id.eIt_expenses_cat);
            cost=itemView.findViewById(R.id.eIt_expenses_cost);
            icon=itemView.findViewById(R.id.eIt_expenses_icon);


        }
    }

    private int getMagnitudeColorAndSetIcon(int cat, ImageView icon) {
        int ColorResourceId;
        int iconResId;
        iconResId=R.drawable.ic_others;
        switch (cat) {
            case 0:
                ColorResourceId = R.color.magnitude1;
                iconResId=R.drawable.ic_restaurant;
                break;
            case 1:
                ColorResourceId = R.color.magnitude2;
                iconResId=R.drawable.ic_shopping;
                break;
            case 2:
                ColorResourceId = R.color.magnitude3;
                iconResId=R.drawable.ic_groceries;
                break;
            case 3:
                ColorResourceId = R.color.magnitude4;
                iconResId=R.drawable.ic_entertanment;
                break;
            case 4:
                ColorResourceId = R.color.magnitude5;
                iconResId=R.drawable.ic_healthy;
                break;
            case 5:
                ColorResourceId = R.color.magnitude6;
                iconResId=R.drawable.ic_others;
                break;
            case 6:
                ColorResourceId = R.color.magnitude7;
                break;
            case 7:
                ColorResourceId = R.color.magnitude8;
                break;
            case 8:
                ColorResourceId = R.color.magnitude9;
                break;
            default:
                ColorResourceId = R.color.magnitude10plus;
                break;
        }
        icon.setImageResource(iconResId);
        return context.getResources().getColor(ColorResourceId);
    }
    private int getCatPos(String cat) {
        String[] stringArray;
        stringArray=context.getResources().getStringArray(R.array.categories);
        int pos=0;
        for(int i=0;i<stringArray.length;i++){
            if(cat.equals(stringArray[i]))
                pos=i;
        }
        return pos;
    }

    OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    interface OnClickListener{
        void onCLick(Expense expense);
    }
}
