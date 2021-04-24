package com.mahmoudjoe3.tracker.ui.fragments.toDo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.mahmoudjoe3.tracker.R;
import com.mahmoudjoe3.tracker.pojo.TodoNote;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ToDoFragment extends Fragment {


    private static final String TAG = "ToDoFragmentTAG";
    @BindView(R.id.toDoRecycle)
    RecyclerView toDoRecycle;
    ToDoAdapter adapter;
    ToDoViewModel model;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    public ToDoFragment() {
        // Required empty public constructor
    }

    private static ToDoFragment fragment;

    public static ToDoFragment newInstance() {
        if (fragment == null)
            fragment = new ToDoFragment();
        return fragment;
    }

    private void observeData() {
        model.getData().subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<TodoNote>>() {
            @Override
            public void onSubscribe(Disposable d) {
                //progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(List<TodoNote> todoNotes) {
                adapter.setNoteList(todoNotes);
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
        adapter = new ToDoAdapter();
        model = new ViewModelProvider(this).get(ToDoViewModel.class);
        observeData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_do, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter.setOnClickListener(new ToDoAdapter.OnClickListener() {
            @Override
            public void onCLick(TodoNote note) {
                showNote_bySheet(note);
            }
        });

        toDoRecycle.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(toDoRecycle);
    }

    private void showNote_bySheet(TodoNote toDonote) {
        BottomSheetDialog sheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        View sheetView = LayoutInflater.from(getActivity()).inflate(R.layout.add_todo_sheet_layout,
                (LinearLayout) sheetDialog.findViewById(R.id.edit_user_name_layout_inner));
        sheetDialog.setContentView(sheetView);

        TextView textView = sheetView.findViewById(R.id.sheet_note);
        textView.setText(toDonote.getNote());
        Button update = sheetView.findViewById(R.id.sheet_save);
        update.setText(R.string.update);
        update.setEnabled(false);
        CharSequence lastText=textView.getText().toString();
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0 && !s.toString().isEmpty()&&!lastText.equals(s)) {
                    update.setEnabled(true);
                } else {
                    update.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        update.setOnClickListener(v -> {
            String note = textView.getText().toString();
            toDonote.setNote(note);
            model.update(toDonote, new ToDoViewModel.OnCompleteListener() {
                @Override
                public void onComplete(TodoNote note) {
                    sheetDialog.dismiss();
                    observeData();
                }
            });
        });
        sheetDialog.show();
    }

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        int adapterSize=0;
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            try {
                final int position = viewHolder.getAdapterPosition();
                adapterSize=adapter.getItemCount();

                final TodoNote item = adapter.removeNote(position);
                Snackbar snackbar = Snackbar.make(viewHolder.itemView, item.getNote() + " "+getString(R.string.is)+" " + (direction == ItemTouchHelper.RIGHT ? getString(R.string.deleted) : getString(R.string.done)), Snackbar.LENGTH_LONG);
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
                        if(adapterSize!=adapter.getItemCount()) {
                            model.delete(item, new ToDoViewModel.OnCompleteListener() {
                                @Override
                                public void onComplete(TodoNote note) {
                                }
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


    @OnClick(R.id.addButtom)
    public void onViewClicked() {
        AddNoteBySheet();
    }

    private void AddNoteBySheet() {
        BottomSheetDialog sheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        View sheetView = LayoutInflater.from(getActivity()).inflate(R.layout.add_todo_sheet_layout,
                (LinearLayout) sheetDialog.findViewById(R.id.edit_user_name_layout_inner));
        sheetDialog.setContentView(sheetView);

        TextView textView = sheetView.findViewById(R.id.sheet_note);
        Button save = sheetView.findViewById(R.id.sheet_save);
        save.setEnabled(false);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0 && !s.toString().isEmpty()) {
                    save.setEnabled(true);
                } else {
                    save.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        save.setOnClickListener(v -> {
            String note = textView.getText().toString();
            TodoNote todoNote = new TodoNote(note);
            //adapter.addNote(todoNote,adapter.getItemCount());
            //todo send to database
            model.insert(todoNote, new ToDoViewModel.OnCompleteListener() {
                @Override
                public void onComplete(TodoNote note) {
                    //adapter.addNote(todoNote,adapter.getItemCount());
                    sheetDialog.dismiss();
                    observeData();

                }
            });
        });


        sheetDialog.show();
    }
}