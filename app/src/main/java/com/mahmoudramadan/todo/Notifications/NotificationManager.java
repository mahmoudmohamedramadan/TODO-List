package com.mahmoudramadan.todo.Notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.mahmoudramadan.todo.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NotificationManager {

    public static void scheduleNotification(String[] taskData) throws ParseException {
        Intent notificationIntent = new Intent(MainActivity.activity, NotificationPublisher.class);
        notificationIntent.putExtra("task", taskData[0]);
        notificationIntent.putExtra("category_id", taskData[2]);

        SimpleDateFormat sDFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sDFormat.parse(taskData[1]));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.activity,
                (int) System.currentTimeMillis(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) MainActivity.activity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
