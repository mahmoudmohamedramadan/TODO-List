package com.mahmoudramadan.todolist.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mahmoudramadan.todolist.Model.TODOModel;

import java.util.ArrayList;
import java.util.List;

public class TODODatabaseHandler extends SQLiteOpenHelper {

    public SQLiteDatabase db;
    public static final int VERSION = 1;
    public static final String NAME = "TODOListDatabase.db";
    public static final String TODO_TABLE = "todos";
    public static final String ID = "id";
    public static final String COLUMN_NAME_TASK = "task";
    public static final String COLUMN_NAME_STATUS = "status";
    public static final String COLUMN_NAME_DATE = "date";
    public static final String COLUMN_NAME_CATEGORY_ID = "category_id";

    private static final String CREATE_TODO_TABLE = "CREATE TABLE IF NOT EXISTS " + TODO_TABLE + "(" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NAME_TASK + " TEXT," +
            COLUMN_NAME_STATUS + " INTEGER," +
            COLUMN_NAME_DATE + " TEXT," +
            COLUMN_NAME_CATEGORY_ID + " INTEGER," +
            "FOREIGN KEY(" + COLUMN_NAME_CATEGORY_ID + ") REFERENCES categories(id))";

    public TODODatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the older tables
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        // Create tables again
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTask(TODOModel task) {
        ContentValues cv = new ContentValues();
        cv.put("task", task.getTask());
        cv.put("status", 0);
        cv.put("date", task.getDate());
        cv.put("category_id", task.getCategory_id());
        db.insert(TODO_TABLE, null, cv);
    }

    public List<TODOModel> getTasks(String query, String[] selectionArgs) {
        List<TODOModel> taskList = new ArrayList<>();
        Cursor cursor = null;
        db.beginTransaction();
        try {
            cursor = db.query(TODO_TABLE, null, query, selectionArgs, null, null, "status ASC", null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        TODOModel task = new TODOModel();
                        task.setId(cursor.getInt(cursor.getColumnIndex("id")));
                        task.setTask(cursor.getString(cursor.getColumnIndex("task")));
                        task.setDate(cursor.getString(cursor.getColumnIndex("date")));
                        task.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                        taskList.add(task);
                    } while (cursor.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            cursor.close();
        }
        return taskList;
    }

    public int getTaskCount(String task, int category_id) {
        return db.query(TODO_TABLE, new String[]{"task"}, "task =? AND category_id=?",
                new String[]{task, String.valueOf(category_id)}, null, null, null, "1").getCount();
    }

    public void updateTaskText(int id, String newTaskValue) {
        ContentValues cv = new ContentValues();
        cv.put("task", newTaskValue);
        db.update(TODO_TABLE, cv, "id=?", new String[]{String.valueOf(id)});
    }

    public void updateTaskStatus(int id, int newStatusValue) {
        ContentValues cv = new ContentValues();
        cv.put("status", newStatusValue);
        db.update(TODO_TABLE, cv, "id=?", new String[]{String.valueOf(id)});
    }

    public void updateTaskDate(int id, String newTaskDate) {
        ContentValues cv = new ContentValues();
        cv.put("date", newTaskDate);
        db.update(TODO_TABLE, cv, "id=?", new String[]{String.valueOf(id)});
    }

    public void deleteTask(int id) {
        db.delete(TODO_TABLE, "id=?", new String[]{String.valueOf(id)});
    }
}
