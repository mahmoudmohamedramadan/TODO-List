package com.mahmoudramadan.todo.Notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.mahmoudramadan.todo.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NotificationManager {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void scheduleNotification(int taskID, String[] taskData) throws ParseException {
        Intent notificationIntent = new Intent(MainActivity.activity, NotificationPublisher.class);
        notificationIntent.putExtra("task", taskData[0]);
        notificationIntent.putExtra("category_id", taskData[2]);

        SimpleDateFormat sDFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sDFormat.parse(taskData[1]));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.activity, taskID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) MainActivity.activity.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
