package com.mahmoudramadan.todolist;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mahmoudramadan.todolist.Adapter.TODOAdapter;
import com.mahmoudramadan.todolist.Model.TODOModel;
import com.mahmoudramadan.todolist.Utils.TODODatabaseHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TasksActivity extends AppCompatActivity implements DialogCloseListener {

    private RecyclerView tasksRecycleView;
    private TODOAdapter tasksAdapter;
    private List<TODOModel> taskList;
    private TODODatabaseHandler db;
    private FloatingActionButton addNewTaskButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_layout);
        getSupportActionBar().hide();

        db = new TODODatabaseHandler(this);
        db.openDatabase();
        db.onCreate(db.db);

        taskList = new ArrayList<>();

        tasksRecycleView = findViewById(R.id.tasksRecycleView);
        tasksRecycleView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new TODOAdapter(db, this);
        tasksRecycleView.setAdapter(tasksAdapter);

        addNewTaskButton = findViewById(R.id.addNewTaskButton);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TODORecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecycleView);

        taskList = db.getTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);

        addNewTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        taskList = db.getTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();
    }
}