package com.mahmoudramadan.todo.Notifications;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.provider.Settings;


import com.mahmoudramadan.todo.R;
import com.mahmoudramadan.todo.TasksActivity;
import com.mahmoudramadan.todo.Utils.CategoryDatabaseHandler;

public class NotificationPublisher extends BroadcastReceiver {

    @Override
    @SuppressLint("WrongConstant")
    public void onReceive(Context context, Intent intent) {
        // get name category via id which passed from intent to use it in TasksActivity to do NOT open favorite list
        CategoryDatabaseHandler db = new CategoryDatabaseHandler(context);
        db.openDatabase();

        Intent tasksActivityIntent = new Intent(context, TasksActivity.class);
        tasksActivityIntent.putExtra("category_id", intent.getStringExtra("category_id"));
        tasksActivityIntent.putExtra("category", db.getCategory(Integer.parseInt(intent.getStringExtra("category_id"))));

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, tasksActivityIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

        Notification notification = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(intent.getStringExtra("task"))
                .addAction(R.drawable.ic_baseline_add, "Open", pendingIntent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI, AudioManager.STREAM_NOTIFICATION)
                .setSmallIcon(R.mipmap.ic_launcher_todo)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_todo))
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), notification);
    }
}