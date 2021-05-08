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
        // RecyclerView
        RecyclerView tasksRecycleView = findViewById(R.id.tasksRecycleView);
        tasksRecycleView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new TODOAdapter(db, this);
        tasksRecycleView.setAdapter(tasksAdapter);
        // addNewTaskButton FloatingActionButton
        FloatingActionButton addNewTaskButton = findViewById(R.id.addNewTaskButton);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TODORecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecycleView);
        // returnToCategoryButton FloatingActionButton
        FloatingActionButton returnToCategoryButton = findViewById(R.id.returnToCategoryButton);
        // TextView
        TextView taskTextView = findViewById(R.id.taskTextView);
        // SearchView
        SearchView taskSearchView = findViewById(R.id.taskSearchView);
        // if intent has category_id then the opened intent from category button click else it actually from favorites button
        if (getIntent().hasExtra("category_id")) {
            taskList = db.getTasks("category_id =?", new String[]{String.valueOf(getIntent().getStringExtra("category_id"))});
            taskTextView.setText(getIntent().getStringExtra("category"));
            taskSearchView.setQueryHint(getString(R.string.search_in_tasks) + " " + getIntent().getStringExtra("category"));
            addNewTaskButton.setVisibility(View.VISIBLE);
        } else {
            taskList = db.getTasks("favorite =?", new String[]{"1"});
            taskTextView.setText(getString(R.string.favorites));
            taskSearchView.setQueryHint(getString(R.string.search_favorites));
            addNewTaskButton.setVisibility(View.INVISIBLE);
        }
        tasksAdapter.setTasks(taskList);
        // add addNewTaskButton's onClickListener
        addNewTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.category_id = getIntent().getStringExtra("category_id");
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });
        // add returnToCategoryButton's onClickListener
        returnToCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // add taskSearchView's onQueryTextListener
        taskSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (taskTextView.getText() != getString(R.string.favorites))
                    taskList = db.getTasks("category_id =? AND task LIKE ?",
                            new String[]{String.valueOf(getIntent().getStringExtra("category_id")), newText + "%"});
                else
                    taskList = db.getTasks("favorite=? AND task LIKE ?", new String[]{"1", newText + "%"});
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