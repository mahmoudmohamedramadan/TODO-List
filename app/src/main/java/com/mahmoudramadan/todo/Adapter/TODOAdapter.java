package com.mahmoudramadan.todo.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
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

    public TODOAdapter(TODODatabaseHandler db, TasksActivity activity) {
        this.db = db;
        this.activity = activity;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        db.openDatabase();
        final TODOModel item = todoList.get(position);
        holder.todoCheckBox.setText(item.getTask());
        holder.selectedDateTextView.setText(item.getDate());
        holder.todoCheckBox.setChecked(toBoolean(item.getStatus()));

        if (holder.todoCheckBox.isChecked())
            holder.todoCheckBox.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        else
            holder.todoCheckBox.setPaintFlags(Paint.ANTI_ALIAS_FLAG);

        if (item.getFavorite() == 0)
            holder.addToFavouriteImageView.setImageResource(R.drawable.ic_baseline_non_favorite);
        else
            holder.addToFavouriteImageView.setImageResource(R.drawable.ic_baseline_favorite);

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
                updateItemTaskDate(holder.itemView, position);
            }
        });

        holder.addToFavouriteImageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (holder.addToFavouriteImageView.getTag().toString() == "non_favourite") {
                    db.updateTaskFavorite(item.getId(), 1);
                    holder.addToFavouriteImageView.setImageResource(R.drawable.ic_baseline_favorite);
                    holder.addToFavouriteImageView.setTag("favourite");
                } else {
                    db.updateTaskFavorite(item.getId(), 0);
                    holder.addToFavouriteImageView.setImageResource(R.drawable.ic_baseline_non_favorite);
                    holder.addToFavouriteImageView.setTag("non_favourite");
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

    public void updateItemTaskDate(View view, int position) {
        TODOModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("date", item.getDate());
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
        TextView selectedDateTextView;
        FloatingActionButton datePickerButton;
        ImageView addToFavouriteImageView;

        public ViewHolder(View view) {
            super(view);
            todoCheckBox = view.findViewById(R.id.todoCheckBox);
            selectedDateTextView = view.findViewById(R.id.selectedDateTextView);
            datePickerButton = view.findViewById(R.id.datePickerButton);
            addToFavouriteImageView = view.findViewById(R.id.addToFavouriteImageView);
        }
    }
}

