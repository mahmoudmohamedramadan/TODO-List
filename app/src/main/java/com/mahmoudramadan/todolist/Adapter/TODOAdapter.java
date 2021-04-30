package com.mahmoudramadan.todolist.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mahmoudramadan.todolist.AddNewTask;
import com.mahmoudramadan.todolist.MainActivity;
import com.mahmoudramadan.todolist.Model.TODOModel;
import com.mahmoudramadan.todolist.R;
import com.mahmoudramadan.todolist.TaskDateActivity;
import com.mahmoudramadan.todolist.Utils.DatabaseHandler;

import java.util.List;

public class TODOAdapter extends RecyclerView.Adapter<TODOAdapter.ViewHolder> {

    private List<TODOModel> todoList;
    private MainActivity activity;
    private DatabaseHandler db;
    private static FloatingActionButton datePickerButton;

    public TODOAdapter(DatabaseHandler db, MainActivity activity) {
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
        holder.todoCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) db.updateTaskStatus(item.getId(), 1);
                else db.updateTaskStatus(item.getId(), 0);
            }
        });

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* TaskDateActivity.newInstance(holder.itemView)
                        .show(((AppCompatActivity) getContext()).getSupportFragmentManager(), TaskDateActivity.TAG); */
                updateItemTaskDate(holder.itemView, position);
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

        public ViewHolder(View view) {
            super(view);
            todoCheckBox = view.findViewById(R.id.todoCheckBox);
            selectedDateTextView = view.findViewById(R.id.selectedDateTextView);
            datePickerButton = view.findViewById(R.id.datePickerButton);
        }
    }
}
