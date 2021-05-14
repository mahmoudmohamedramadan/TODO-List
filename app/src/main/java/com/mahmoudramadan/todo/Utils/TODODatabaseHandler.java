package com.mahmoudramadan.todo.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.mahmoudramadan.todo.Model.TODOModel;
import com.mahmoudramadan.todo.Notifications.NotificationManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TODODatabaseHandler extends SQLiteOpenHelper {

    public SQLiteDatabase db;
    public static final int VERSION = 1;
    public static final String NAME = "TODOListDatabase.db";
    public static final String TODO_TABLE = "todos";
    public static final String ID = "id";
    public static final String COLUMN_NAME_TASK = "task";
    public static final String COLUMN_NAME_STATUS = "status";
    public static final String COLUMN_NAME_DATETIME = "date_time";
    public static final String COLUMN_NAME_FAVORITE = "favorite";
    public static final String COLUMN_NAME_CATEGORY_ID = "category_id";

    private static final String CREATE_TODO_TABLE = "CREATE TABLE IF NOT EXISTS " + TODO_TABLE + "(" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NAME_TASK + " TEXT," +
            COLUMN_NAME_STATUS + " INTEGER," +
            COLUMN_NAME_DATETIME + " TEXT," +
            COLUMN_NAME_FAVORITE + " INTEGER," +
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
        cv.put("date_time", task.getDate_time());
        cv.put("favorite", task.getFavorite());
        cv.put("category_id", task.getCategory_id());
        db.insert(TODO_TABLE, null, cv);
    }

    public List<TODOModel> getTasks(String query, String[] selectionArgs) {
        List<TODOModel> taskList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(TODO_TABLE, null, query, selectionArgs, null, null, "status ASC", null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        TODOModel task = new TODOModel();
                        task.setId(cursor.getInt(cursor.getColumnIndex("id")));
                        task.setTask(cursor.getString(cursor.getColumnIndex("task")));
                        task.setDate_time(cursor.getString(cursor.getColumnIndex("date_time")));
                        task.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                        task.setFavorite(cursor.getInt(cursor.getColumnIndex("favorite")));
                        taskList.add(task);
                    } while (cursor.moveToNext());
                }
            }
        } finally {
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

    public void updateTaskDateTime(int id, String newTaskDateTime) {
        ContentValues cv = new ContentValues();
        cv.put("date_time", newTaskDateTime);
        db.update(TODO_TABLE, cv, "id=?", new String[]{String.valueOf(id)});
    }

    public void updateTaskFavorite(int id, int newTaskFavorite) {
        ContentValues cv = new ContentValues();
        cv.put("favorite", newTaskFavorite);
        db.update(TODO_TABLE, cv, "id=?", new String[]{String.valueOf(id)});
    }

    public void deleteTask(int id) {
        db.delete(TODO_TABLE, "id=?", new String[]{String.valueOf(id)});
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void pushNotification() {
        Calendar calendar = Calendar.getInstance();

        Cursor cursor = null;
        try {
            cursor = db.query(TODO_TABLE, new String[]{"id", "task", "date_time", "category_id"}, "date_time!=''", null,
                    null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        int taskID = cursor.getInt(cursor.getColumnIndex("id"));
                        String task = cursor.getString(cursor.getColumnIndex("task"));
                        String dateTime = cursor.getString(cursor.getColumnIndex("date_time"));
                        String categoryId = cursor.getString(cursor.getColumnIndex("category_id"));
                        calendar.setTime(new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(dateTime));
                        if (new Date().before(calendar.getTime())) {
                            NotificationManager.scheduleNotification(taskID, new String[]{task, dateTime, categoryId});
                        }
                    } while (cursor.moveToNext());
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }
}
