package com.mahmoudramadan.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    private CategoryAdapter categoriesAdapter;
    private List<CategoryModel> categoryList;
    private CategoryDatabaseHandler db;
    public static MainActivity activity;

    private static void newInstance(MainActivity activity) {
        MainActivity.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load stored locale of my application
        LocaleManager.loadLocale(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        newInstance(this);

        db = new CategoryDatabaseHandler(this);
        db.openDatabase();
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
        // favoritesImageButton
        ImageButton favoritesImageButton = findViewById(R.id.favoritesImageButton);
        // chooseSoundNotificationImageButton
        ImageButton chooseSoundNotificationImageButton = findViewById(R.id.chooseSoundNotificationImageButton);

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
        // add chooseSoundNotificationImageButton's onClickListener
        chooseSoundNotificationImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
                String tone = preferences.getString("tone", "");

                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.choose_notification_tone));
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(tone));
                startActivityForResult(intent, 5);
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
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK && requestCode == 5) {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null) {
                SharedPreferences.Editor editor = activity.getSharedPreferences("Settings", activity.MODE_PRIVATE).edit();
                editor.putString("tone", uri.toString());
                editor.apply();
            }
        }
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        categoryList = db.getCategories(null, null);
        Collections.reverse(categoryList);
        categoriesAdapter.setCategories(categoryList);
        categoriesAdapter.notifyDataSetChanged();
    }
}