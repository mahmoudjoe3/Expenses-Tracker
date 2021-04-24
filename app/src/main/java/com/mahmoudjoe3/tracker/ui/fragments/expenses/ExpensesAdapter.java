package com.mahmoudjoe3.tracker.ui.fragments.expenses;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.mahmoudjoe3.tracker.R;
import com.mahmoudjoe3.tracker.pojo.Expense;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.VH> {

    private static final String TAG = "check_time";
    List<Expense> expenseList;
    Context context;

    private MutableLiveData<Float> monthly_spent=new MutableLiveData<>();
    Float mon=0.0f;
    public LiveData<Float> getMonthly_spent() {
        monthly_spent.setValue(mon);
        return monthly_spent;
    }

    private MutableLiveData<Float> pre_month_spent=new MutableLiveData<>();
    Float pre_mon=0.0f;
    public LiveData<Float> getPre_month_spent() {
        pre_month_spent.setValue(pre_mon);
        return pre_month_spent;
    }

    private MutableLiveData<Float> daily_spent=new MutableLiveData<>();
    Float daily=0.0f;
    public LiveData<Float> getDaily_spent() {
        daily_spent.setValue(daily);
        return daily_spent;
    }

    public ExpensesAdapter(Context context1) {
        this.expenseList = new ArrayList<>();
        context=context1;
    }

    public void setExpenseList(List<Expense> expenseList) {
        mon=0.0f;
        daily=0.0f;
        pre_mon=0.0f;
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

        if(iscurrentMonth(n)){
            if(isCurrentDay(n)) {
                daily-=(n.getPrice()*n.getAmount());
                daily_spent.setValue(daily);
            }
            mon-=(n.getPrice()*n.getAmount());
            monthly_spent.setValue(mon);
        }else {
            pre_mon -= (n.getPrice() * n.getAmount());
            pre_month_spent.setValue(pre_mon);
        }

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
        //spent at this month

        float cost=(expense.getAmount()*expense.getPrice());
        setSpent(expense, cost);

        holder.cat.setText(expense.getCat());
        holder.cost.setText("$"+cost);

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

    private void setSpent(Expense expense, float cost) {
        if(iscurrentMonth(expense)){
            if(isCurrentDay(expense)) {
                daily+=cost;
                daily_spent.setValue(daily);
            }
            mon+=cost;
            monthly_spent.setValue(mon);
        }else {
            pre_mon +=cost;
            pre_month_spent.setValue(pre_mon);
        }
    }

    private boolean iscurrentMonth(Expense expense) {
        Calendar c=Calendar.getInstance();
        String datetxt= java.text.DateFormat.getDateInstance().format(c.getTime());
        return datetxt.startsWith(expense.getDate().substring(0,3));
    }
    private boolean isCurrentDay(Expense expense) {
        Calendar c=Calendar.getInstance();
        String datetxt= java.text.DateFormat.getDateInstance().format(c.getTime());
        return datetxt.equals(expense.getDate());
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
