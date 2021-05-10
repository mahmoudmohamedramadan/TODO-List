package com.mahmoudramadan.todo.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mahmoudramadan.todo.AddNewTask;
import com.mahmoudramadan.todo.Model.TODOModel;
import com.mahmoudramadan.todo.R;
import com.mahmoudramadan.todo.TaskDateActivity;
import com.mahmoudramadan.todo.TasksActivity;
import com.mahmoudramadan.todo.Utils.TODODatabaseHandler;

import java.util.List;

public class TODOAdapter extends RecyclerView.Adapter<TODOAdapter.ViewHolder> {

    private List<TODOModel> todoList;
    private TasksActivity activity;
    private TODODatabaseHandler db;
    private boolean imageButtonIsClicked =false;

    public TODOAdapter(TODODatabaseHandler db, TasksActivity activity) {
        this.db = db;
        this.activity = activity;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        db.openDatabase();
        final TODOModel item = todoList.get(position);
        holder.todoCheckBox.setText(item.getTask());
        holder.selectedDateTimeTextView.setText(item.getDate_time());
        holder.todoCheckBox.setChecked(toBoolean(item.getStatus()));

        if (holder.todoCheckBox.isChecked())
            holder.todoCheckBox.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        else
            holder.todoCheckBox.setPaintFlags(Paint.ANTI_ALIAS_FLAG);

        if (item.getFavorite() == 0)
            holder.addToFavouriteImageButton.setBackground(getContext().getDrawable(R.drawable.ic_baseline_non_favorite));
        else
            holder.addToFavouriteImageButton.setBackground(getContext().getDrawable(R.drawable.ic_baseline_favorite));

        holder.todoCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.todoCheckBox.isChecked()) {
                    db.updateTaskStatus(item.getId(), 1);
                    holder.todoCheckBox.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    db.updateTaskStatus(item.getId(), 0);
                    holder.todoCheckBox.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
                }
            }
        });

        holder.datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItemTaskDateTime(holder.itemView, position);
            }
        });

        holder.addToFavouriteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonIsClicked = !imageButtonIsClicked;
                if (imageButtonIsClicked) {
                    db.updateTaskFavorite(item.getId(), 1);
                    holder.addToFavouriteImageButton.setBackground(getContext().getDrawable(R.drawable.ic_baseline_favorite));
                } else {
                    db.updateTaskFavorite(item.getId(), 0);
                    holder.addToFavouriteImageButton.setBackground(getContext().getDrawable(R.drawable.ic_baseline_non_favorite));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    private boolean toBoolean(int status) {
        return status == 1;
    }

    public Context getContext() {
        return activity;
    }

    public void setTasks(List<TODOModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public void updateItemTaskText(int position) {
        TODOModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public void updateItemTaskDateTime(View view, int position) {
        TODOModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("date_time", item.getDate_time());
        TaskDateActivity fragment = new TaskDateActivity(view);
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), TaskDateActivity.TAG);
    }

    public void deleteItem(int position) {
        TODOModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox todoCheckBox;
        TextView selectedDateTimeTextView;
        FloatingActionButton datePickerButton;
        ImageButton addToFavouriteImageButton;

        public ViewHolder(View view) {
            super(view);
            todoCheckBox = view.findViewById(R.id.todoCheckBox);
            selectedDateTimeTextView = view.findViewById(R.id.selectedDateTimeTextView);
            datePickerButton = view.findViewById(R.id.datePickerButton);
            addToFavouriteImageButton = view.findViewById(R.id.addToFavouriteImageButton);
        }
    }
}

