package com.mahmoudramadan.todo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mahmoudramadan.todo.Adapter.TODOAdapter;
import com.mahmoudramadan.todo.Model.TODOModel;
import com.mahmoudramadan.todo.Utils.TODODatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class TasksActivity extends AppCompatActivity implements DialogCloseListener {

    private TODOAdapter tasksAdapter;
    private List<TODOModel> taskList;
    private TODODatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_layout);
        getSupportActionBar().hide();

        db = new TODODatabaseHandler(this);
        db.openDatabase();
        db.onCreate(db.db);

        taskList = new ArrayList<>();

        RecyclerView tasksRecycleView = findViewById(R.id.tasksRecycleView);
        tasksRecycleView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new TODOAdapter(db, this);
        tasksRecycleView.setAdapter(tasksAdapter);

        FloatingActionButton addNewTaskButton = findViewById(R.id.addNewTaskButton);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TODORecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecycleView);

        FloatingActionButton returnToCategoryButton = findViewById(R.id.returnToCategoryButton);

        TextView taskNameEditText = findViewById(R.id.taskNameEditText);
        taskNameEditText.setText(getIntent().getStringExtra("category"));

        SearchView taskSearchView = findViewById(R.id.taskSearchView);
        taskSearchView.setQueryHint("Search in " + getIntent().getStringExtra("category"));

        taskList = db.getTasks("category_id =?", new String[]{String.valueOf(getIntent().getStringExtra("category_id"))});
        tasksAdapter.setTasks(taskList);

        addNewTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.category_id = getIntent().getStringExtra("category_id");
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

        returnToCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        taskSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                taskList = db.getTasks("category_id =? AND task LIKE ?",
                        new String[]{String.valueOf(getIntent().getStringExtra("category_id")), newText + "%"});
                tasksAdapter.setTasks(taskList);
                tasksAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        taskList = db.getTasks("category_id =?", new String[]{String.valueOf(getIntent().getStringExtra("category_id"))});
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();
    }
}