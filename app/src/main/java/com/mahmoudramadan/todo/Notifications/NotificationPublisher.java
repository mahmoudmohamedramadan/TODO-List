package com.mahmoudramadan.todo.Notifications;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.SyncStateContract;

import androidx.core.app.NotificationCompat;

import com.mahmoudramadan.todo.R;
import com.mahmoudramadan.todo.TasksActivity;
import com.mahmoudramadan.todo.Utils.CategoryDatabaseHandler;

public class NotificationPublisher extends BroadcastReceiver {

    String CHANNEL_ID = SyncStateContract.Constants._ID;
    String CHANNEL_NAME = "TODO";

    @Override
    @SuppressLint("WrongConstant")
    public void onReceive(Context context, Intent intent) {
        // get name category via id which passed from intent to use it in TasksActivity to do NOT open favorite list
        CategoryDatabaseHandler db = new CategoryDatabaseHandler(context);
        db.openDatabase();

        Intent tasksActivityIntent = new Intent(context, TasksActivity.class);
        tasksActivityIntent.putExtra("category_id", intent.getStringExtra("category_id"));
        tasksActivityIntent.putExtra("category", db.getCategory(Integer.parseInt(intent.getStringExtra("category_id"))));

        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(),
                tasksActivityIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        SharedPreferences preferences = context.getSharedPreferences("Settings", context.MODE_PRIVATE);
        String tone = preferences.getString("tone", "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableVibration(true);
            channel.setLightColor(Color.BLUE);
            channel.enableLights(true);
            if (channel.canShowBadge()) channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(intent.getStringExtra("task"))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(Uri.parse(tone))
                .setSmallIcon(R.drawable.ic_baseline_notifications)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_todo));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId(CHANNEL_ID);
        }

        notificationManager.notify((int) System.currentTimeMillis(), notification.build());
    }
}