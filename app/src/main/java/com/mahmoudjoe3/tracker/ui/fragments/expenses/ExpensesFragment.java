package com.mahmoudjoe3.tracker.ui.fragments.expenses;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mahmoudjoe3.tracker.R;
import com.mahmoudjoe3.tracker.pojo.Expense;
import com.mahmoudjoe3.tracker.pojo.TodoNote;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ExpensesFragment extends Fragment {

    @BindView(R.id.totalSpent_this_month)
    TextView totalSpentThisMonth;
    @BindView(R.id.totalSpent_previous_month)
    TextView totalSpentPreviousMonth;
    @BindView(R.id.ex_recycle)
    RecyclerView exRecycle;
    ExpensesAdapter adapter;

    public ExpensesFragment() {
        // Required empty public constructor
    }

    private static ExpensesFragment fragment;

    public static ExpensesFragment newInstance() {
        if (fragment == null)
            fragment = new ExpensesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.ex_addButtom)
    public void onViewClicked() {
        addExpensesBySheet(null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<Expense> expenseList=new ArrayList<>();
        expenseList.add(new Expense(null,1,20,"Cafa","Oct 2 2021","7:05 PM","Restaurants"));
        expenseList.add(new Expense(null,1,2000,"Macbook pro","Oct 2 2021","7:05 PM","Shopping"));
        expenseList.add(new Expense(null,1,100,"test","Oct 2 2021","7:05 PM","Groceries"));
        expenseList.add(new Expense(null,4,20,"Gym","Oct 2 2021","7:05 PM","Entertainment"));
        expenseList.add(new Expense(null,2,500,"flue","Oct 2 2021","7:05 PM","Healthy"));
        expenseList.add(new Expense(null,1,10,"wake up","Oct 2 2021","7:05 PM","Others"));
        expenseList.add(new Expense(null,1,2000,"fancy dinner","Oct 2 2021","7:05 PM","Restaurants"));
        adapter=new ExpensesAdapter(expenseList,getActivity());
        adapter.setOnClickListener(new ExpensesAdapter.OnClickListener() {
            @Override
            public void onCLick(Expense expense) {
                addExpensesBySheet(expense);
            }
        });
        exRecycle.setAdapter(adapter);

    }

    private void addExpensesBySheet(Expense expense) {
        BottomSheetDialog sheetDialog = new BottomSheetDialog(getActivity(),R.style.BottomSheetDialogTheme);
        View sheetView= LayoutInflater.from(getActivity()).inflate(R.layout.add_expenses_sheet_layout,
                (LinearLayout)sheetDialog.findViewById(R.id.add_expenses_sheet));
        sheetDialog.setContentView(sheetView);
        sheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog dialogc = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet =  dialogc.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        TextView amount,price,description,date,time;
        amount=sheetView.findViewById(R.id.ex_amount);
        price=sheetView.findViewById(R.id.ex_price);
        description=sheetView.findViewById(R.id.ex_description);
        date=sheetView.findViewById(R.id.ex_date);
        time=sheetView.findViewById(R.id.ex_time);
        Spinner catSpinner=sheetView.findViewById(R.id.ex_cat);
        Button save = sheetView.findViewById(R.id.sheet_save);
        Button delete = sheetView.findViewById(R.id.sheet_delete);

        date.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (date.getRight() - date.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        openDateDialoge(date);
                        return true;
                    }
                }
                return false;
            }
        });

        time.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (time.getRight() - time.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        opentimeDialoge(time);
                        return true;
                    }
                }
                return false;
            }
        });
        //add button
        if(expense==null){
            save.setTag("save");
            delete.setVisibility(View.GONE);
        }else {//update process
            save.setText("Update");
            save.setTag("update");
            amount.setText(expense.getAmount()+"");
            price.setText(""+expense.getPrice());
            description.setText(expense.getDescription());
            date.setText(expense.getDate());
            time.setText(expense.getTime());
            catSpinner.setSelection(getCatPos(getResources().getStringArray(R.array.categories),expense.getCat()));
        }

        save.setOnClickListener(v -> {
            if(save.getTag().equals("save")){

                //todo save in database
            }else {//update
                //todo update in database
            }
            sheetDialog.dismiss();
        });

        sheetView.findViewById(R.id.sheet_cancel).setOnClickListener(v -> {
            sheetDialog.dismiss();
        });

        sheetDialog.show();
    }

    private void opentimeDialoge(TextView time) {
        // Get Current Time
        int mHour,mMinute;
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        time.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void openDateDialoge(TextView date){
        // Get Current Date
        int mYear,mMonth,mDay;
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private int getCatPos(String[] stringArray,String cat) {
        int pos=0;
        for(int i=0;i<stringArray.length;i++){
            if(cat.equals(stringArray[i]))
                pos=i;
        }
        return pos;
    }
}