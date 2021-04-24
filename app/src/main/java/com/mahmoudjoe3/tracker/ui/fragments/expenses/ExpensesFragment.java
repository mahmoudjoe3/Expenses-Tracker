package com.mahmoudjoe3.tracker.ui.fragments.expenses;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mahmoudjoe3.tracker.R;
import com.mahmoudjoe3.tracker.pojo.Expense;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class ExpensesFragment extends Fragment {

    private static final String TAG = "ExpensesFragment";
    @BindView(R.id.totalSpent_this_month)
    TextView totalSpentThisMonth;
    @BindView(R.id.totalSpent_previous_month)
    TextView totalSpentPreviousMonth;
    @BindView(R.id.ex_recycle)
    RecyclerView exRecycle;
    ExpensesAdapter adapter;
    ExpenseViewModel model;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.totalSpent_today)
    TextView totalSpentToday;
    @BindView(R.id.ex_addButtom)
    FloatingActionButton exAddButtom;

    public ExpensesFragment() {
        // Required empty public constructor
    }

    private static ExpensesFragment fragment;

    public static ExpensesFragment newInstance() {
        if (fragment == null)
            fragment = new ExpensesFragment();
        return fragment;
    }

    private void observeData() {
        model.getData().subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Expense>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onSuccess(List<Expense> expenseList) {
                        adapter.setExpenseList(expenseList);
                        Log.d(TAG, "data onSuccess: ");
                        //progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ExpensesAdapter(getActivity());

        model = new ViewModelProvider(this).get(ExpenseViewModel.class);
        observeData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter.getMonthly_spent().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float cost) {
                totalSpentThisMonth.setText(getString(R.string.This_month_youve_spent) + cost);
            }
        });
        adapter.getDaily_spent().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float cost) {
                totalSpentToday.setText("$" + cost);
            }
        });
        adapter.getPre_month_spent().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float cost) {
                totalSpentPreviousMonth.setText(getString(R.string.previous_months) + cost);
            }
        });
        adapter.setOnClickListener(new ExpensesAdapter.OnClickListener() {
            @Override
            public void onCLick(Expense expense) {
                addExpensesBySheet(expense);
            }
        });
        exRecycle.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(exRecycle);
    }

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        int adapterSize = 0;

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            try {
                final int position = viewHolder.getAdapterPosition();
                adapterSize = adapter.getItemCount();

                final Expense item = adapter.removeNote(position);
                Snackbar snackbar = Snackbar.make(viewHolder.itemView, item.getDescription() + " "+getString(R.string.is)+" " + (direction == ItemTouchHelper.RIGHT ? getString(R.string.deleted) : getString(R.string.done)) , Snackbar.LENGTH_LONG);
                snackbar.setAction(android.R.string.cancel, new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        try {
                            adapter.addNote(item, position);
                        } catch (Exception e) {
                            Log.e("MainActivity", e.getMessage());
                        }
                    }
                });
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (adapterSize != adapter.getItemCount()) {
                            model.delete(item, expense -> {
                            });
                        }
                    }
                });
                snackbar.show();

            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage());
            }
        }

        // You must use @RecyclerViewSwipeDecorator inside the onChildDraw method
        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(), R.color.material_green_a200))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_done_24)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getActivity(), R.color.material_red_a200))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeRightLabel(getString(R.string.delete))
                    .setSwipeRightLabelColor(Color.WHITE)
                    .addSwipeLeftLabel(getString(R.string.done))
                    .setSwipeLeftLabelColor(Color.WHITE)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


    @OnClick(R.id.ex_addButtom)
    public void onViewClicked() {
        addExpensesBySheet(null);
    }

    private void addExpensesBySheet(Expense expense) {
        BottomSheetDialog sheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        View sheetView = LayoutInflater.from(getActivity()).inflate(R.layout.add_expenses_sheet_layout,
                (LinearLayout) sheetDialog.findViewById(R.id.add_expenses_sheet));
        sheetDialog.setContentView(sheetView);
        sheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog dialogc = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = dialogc.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        TextView amount, price, description, date, time;
        amount = sheetView.findViewById(R.id.ex_amount);
        price = sheetView.findViewById(R.id.ex_price);
        description = sheetView.findViewById(R.id.ex_description);
        date = sheetView.findViewById(R.id.ex_date);
        time = sheetView.findViewById(R.id.ex_time);
        Spinner catSpinner = sheetView.findViewById(R.id.ex_cat);
        Button save = sheetView.findViewById(R.id.sheet_save);
        Button delete = sheetView.findViewById(R.id.sheet_delete);

        date.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (date.getRight() - date.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
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
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (time.getRight() - time.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        //showDateTimePicker();
                        opentimeDialoge(time);
                        return true;
                    }
                }
                return false;
            }
        });
        //add button
        if (expense == null) {
            save.setTag(getString(R.string.save));
            delete.setVisibility(View.GONE);
        } else {//update process
            save.setText(R.string.update);
            save.setTag(R.string.update);
            amount.setText(expense.getAmount() + "");
            price.setText("" + expense.getPrice());
            description.setText(expense.getDescription());
            date.setText(expense.getDate());
            time.setText(expense.getTime());
            catSpinner.setSelection(getCatPos(getResources().getStringArray(R.array.categories), expense.getCat()));
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.delete(expense, new ExpenseViewModel.OnCompleteListener() {
                    @Override
                    public void onComplete(Expense expense) {
                        observeData();
                    }
                });
            }
        });
        save.setOnClickListener(v -> {
            Expense ex = new Expense(Integer.parseInt(amount.getText().toString())
                    , Float.valueOf(price.getText().toString())
                    , description.getText().toString()
                    , date.getText().toString()
                    , time.getText().toString()
                    , catSpinner.getSelectedItem().toString());
            if (Valid(ex)) {
                if (save.getTag().equals("save")) {
                    //todo save in database
                    model.insert(ex, new ExpenseViewModel.OnCompleteListener() {
                        @Override
                        public void onComplete(Expense expense) {
                            observeData();
                        }
                    });
                } else {//update
                    //todo update in database
                    ex.setId(expense.getId());
                    model.update(ex, new ExpenseViewModel.OnCompleteListener() {
                        @Override
                        public void onComplete(Expense expense) {
                            observeData();
                        }
                    });
                }
            } else Toast.makeText(getActivity(), R.string.Invalid_data, Toast.LENGTH_SHORT).show();

            sheetDialog.dismiss();
        });

        sheetView.findViewById(R.id.sheet_cancel).setOnClickListener(v -> {
            sheetDialog.dismiss();
        });

        sheetDialog.show();
    }

    private boolean Valid(Expense expense) {
        if (expense.getPrice() == 0 ||
                expense.getCat().isEmpty() ||
                expense.getDate().isEmpty() ||
                expense.getTime().isEmpty() ||
                expense.getDescription().isEmpty())
            return false;
        return true;
    }


    Calendar date;

    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        Log.v(TAG, "The choosen one " + date.getTime());
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    private void opentimeDialoge(TextView time) {
        // Get Current Time
        Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR);
        int mMinute = c.get(Calendar.MINUTE);
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        CharSequence timetxt = DateFormat.format("hh:mm aa", calendar);
                        time.setText(timetxt);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void openDateDialoge(TextView date) {
        // Get Current Date
        int mYear, mMonth, mDay;
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        String datetxt = java.text.DateFormat.getDateInstance().format(c.getTime());

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                String datetxt = java.text.DateFormat.getDateInstance().format(calendar.getTime());
                                date.setText(datetxt);
                            }
                        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private int getCatPos(String[] stringArray, String cat) {
        int pos = 0;
        for (int i = 0; i < stringArray.length; i++) {
            if (cat.equals(stringArray[i]))
                pos = i;
        }
        return pos;
    }
}