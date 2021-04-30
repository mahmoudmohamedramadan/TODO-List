package com.mahmoudramadan.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mahmoudramadan.todolist.Adapter.CategoryAdapter;
import com.mahmoudramadan.todolist.Model.CategoryModel;
import com.mahmoudramadan.todolist.Utils.CategoryDatabaseHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    private RecyclerView categoriesRecycleView;
    private CategoryAdapter categoriesAdapter;
    private List<CategoryModel> categoryList;
    private CategoryDatabaseHandler db;
    private FloatingActionButton addNewCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        db = new CategoryDatabaseHandler(this);
        db.openDatabase();

        categoryList = new ArrayList<>();

        categoriesRecycleView = findViewById(R.id.categoriesRecycleView);
        categoriesRecycleView.setLayoutManager(new LinearLayoutManager(this));
        categoriesAdapter = new CategoryAdapter(db, this);
        categoriesRecycleView.setAdapter(categoriesAdapter);

        addNewCategory = findViewById(R.id.addNewCategory);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new CategoryRecyclerItemTouchHelper(categoriesAdapter));
        itemTouchHelper.attachToRecyclerView(categoriesRecycleView);

        categoryList = db.getCategories();
        Collections.reverse(categoryList);
        categoriesAdapter.setCategories(categoryList);

        addNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewCategory.newInstance().show(getSupportFragmentManager(), AddNewCategory.TAG);
            }
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        categoryList = db.getCategories();
        Collections.reverse(categoryList);
        categoriesAdapter.setCategories(categoryList);
        categoriesAdapter.notifyDataSetChanged();
    }
}