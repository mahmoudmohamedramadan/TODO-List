package com.mahmoudramadan.todo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mahmoudramadan.todo.Adapter.CategoryAdapter;
import com.mahmoudramadan.todo.Model.CategoryModel;
import com.mahmoudramadan.todo.Utils.CategoryDatabaseHandler;
import com.mahmoudramadan.todo.Utils.TODODatabaseHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    private CategoryAdapter categoriesAdapter;
    private List<CategoryModel> categoryList;
    private CategoryDatabaseHandler db;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load stored locale of my application
        LocaleManager.loadLocale(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        db = new CategoryDatabaseHandler(this);
        db.openDatabase();

        TODODatabaseHandler dbTODO = new TODODatabaseHandler(this);
        dbTODO.openDatabase();
        dbTODO.onCreate(dbTODO.db);
        dbTODO.pushNotification(this);

        categoryList = new ArrayList<>();
        // RecyclerView
        RecyclerView categoriesRecycleView = findViewById(R.id.categoriesRecycleView);
        categoriesRecycleView.setLayoutManager(new LinearLayoutManager(this));
        categoriesAdapter = new CategoryAdapter(db, this);
        categoriesRecycleView.setAdapter(categoriesAdapter);
        // FloatingActionButton
        FloatingActionButton addNewCategoryButton = findViewById(R.id.addNewCategoryButton);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new CategoryRecyclerItemTouchHelper(categoriesAdapter));
        itemTouchHelper.attachToRecyclerView(categoriesRecycleView);
        // SearchView
        SearchView categorySearchView = findViewById(R.id.categorySearchView);
        // BottomAppBar
        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        // ImageView
        ImageButton favoritesImageButton = findViewById(R.id.favoritesImageButton);

        categoryList = db.getCategories(null, null);
        Collections.reverse(categoryList);
        categoriesAdapter.setCategories(categoryList);
        // add addNewCategory's onClickListener
        addNewCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewCategory.newInstance().show(getSupportFragmentManager(), AddNewCategory.TAG);
            }
        });
        // add favoriteImageButton's onClickListener
        favoritesImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TasksActivity.class);
                startActivity(intent);
            }
        });
        // add taskSearchView's onQueryTextListener
        categorySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                categoryList = db.getCategories("category LIKE ?", new String[]{newText + "%"});
                Collections.reverse(categoryList);
                categoriesAdapter.setCategories(categoryList);
                categoriesAdapter.notifyDataSetChanged();
                return true;
            }
        });
        // add bottomAppBar's onMenuItemClickListener
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.ar)
                    LocaleManager.setLocale(MainActivity.this, "ar");
                else
                    LocaleManager.setLocale(MainActivity.this, "en");
                recreate();
                return true;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        //startService( new Intent( this, NotificationService. class )) ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        categoryList = db.getCategories(null, null);
        Collections.reverse(categoryList);
        categoriesAdapter.setCategories(categoryList);
        categoriesAdapter.notifyDataSetChanged();
    }
}