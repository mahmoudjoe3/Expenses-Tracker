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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.mahmoudjoe3.tracker.R;
import com.mahmoudjoe3.tracker.pojo.TodoNote;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ToDoFragment extends Fragment {


    @BindView(R.id.toDoRecycle)
    RecyclerView toDoRecycle;
    ToDoAdapter adapter;

    public ToDoFragment() {
        // Required empty public constructor
    }

    private static ToDoFragment fragment;

    public static ToDoFragment newInstance() {
        if (fragment == null)
            fragment = new ToDoFragment();
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
        View view = inflater.inflate(R.layout.fragment_to_do, container, false);
        ButterKnife.bind(this, view);
        List<TodoNote> notes=new ArrayList<>();
        notes.add(new TodoNote(null,"do that and that"));
        notes.add(new TodoNote(null,"do that and that2"));
        notes.add(new TodoNote(null,"do that and that3"));
        notes.add(new TodoNote(null,"do that and that4"));
        adapter=new ToDoAdapter(notes);
        toDoRecycle.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(toDoRecycle);
        return view;
    }
    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            try {
                final int position = viewHolder.getAdapterPosition();
                final TodoNote item = adapter.removeNote(viewHolder.getAdapterPosition());
                Snackbar snackbar = Snackbar.make(viewHolder.itemView, item.getNote() +" is "+ (direction == ItemTouchHelper.RIGHT ? "deleted" : "Done") + ".", Snackbar.LENGTH_LONG);
                snackbar.setAction(android.R.string.cancel, new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        try {
                            adapter.addNote(item,position);
                        } catch (Exception e) {
                            Log.e("MainActivity", e.getMessage());
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
        public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){
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


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.addButtom)
    public void onViewClicked() {
        showImageBySheet();

    }

    private void showImageBySheet() {
        BottomSheetDialog sheetDialog = new BottomSheetDialog(getActivity(),R.style.BottomSheetDialogTheme);
        View sheetView= LayoutInflater.from(getActivity()).inflate(R.layout.add_todo_sheet_layout,
                (LinearLayout)sheetDialog.findViewById(R.id.edit_user_name_layout_inner));
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
            TodoNote ToDoNote = new TodoNote("id", note);
            adapter.addNote(ToDoNote,adapter.getItemCount());
            //todo send to database

            sheetDialog.dismiss();
        });

        sheetView.findViewById(R.id.sheet_cancel).setOnClickListener(v -> {
            sheetDialog.dismiss();
        });

        sheetDialog.show();
    }
}