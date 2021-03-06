package com.mahmoudramadan.todo.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mahmoudramadan.todo.Model.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryDatabaseHandler extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    public static final int VERSION = 1;
    public static final String NAME = "TODOListDatabase.db";
    public static final String CATEGORY_TABLE = "categories";
    public static final String ID = "id";
    public static final String COLUMN_NAME_CATEGORY = "category";

    private static final String CREATE_CATEGORY_TABLE = "CREATE TABLE IF NOT EXISTS " + CATEGORY_TABLE + "(" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NAME_CATEGORY + " TEXT)";

    public CategoryDatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CATEGORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the older tables
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE);
        // Create tables again
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertCategory(CategoryModel category) {
        ContentValues cv = new ContentValues();
        cv.put("category", category.getCategory());
        db.insert(CATEGORY_TABLE, null, cv);
    }

    public List<CategoryModel> getCategories(String query, String[] selectionArgs) {
        List<CategoryModel> categoryList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(CATEGORY_TABLE, null, query, selectionArgs, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        CategoryModel category = new CategoryModel();
                        category.setId(cursor.getInt(cursor.getColumnIndex("id")));
                        category.setCategory(cursor.getString(cursor.getColumnIndex("category")));
                        categoryList.add(category);
                    } while (cursor.moveToNext());
                }
            }
        } finally {
            cursor.close();
        }
        return categoryList;
    }

    public String getCategory(int id) {
        Cursor cursor = db.query(CATEGORY_TABLE, new String[]{"category"}, "id=?", new String[]{String.valueOf(id)},
                null, null, null, null);
        CategoryModel category = new CategoryModel();
        if (cursor.moveToFirst()) {
            category.setCategory(cursor.getString(cursor.getColumnIndex("category")));
            cursor.close();
        }
        return category.getCategory();
    }

    public int getCategoryCount(String category) {
        return db.query(CATEGORY_TABLE, null, "category=?", new String[]{category},
                null, null, null, null).getCount();
    }

    public void updateCategoryText(int id, String newCategoryValue) {
        ContentValues cv = new ContentValues();
        cv.put("category", newCategoryValue);
        db.update(CATEGORY_TABLE, cv, "id=?", new String[]{String.valueOf(id)});
    }

    public void deleteCategory(int id) {
        db.delete(TODODatabaseHandler.TODO_TABLE, "category_id=?", new String[]{String.valueOf(id)});
        db.delete(CATEGORY_TABLE, "id=?", new String[]{String.valueOf(id)});
    }
}
