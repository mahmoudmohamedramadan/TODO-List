package com.mahmoudramadan.todo.Notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import com.mahmoudramadan.todo.MainActivity;
import com.mahmoudramadan.todo.R;
import com.mahmoudramadan.todo.TasksActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NotificationManager {

    public static void scheduleNotification(Notification notification, MainActivity activity, String dateTime) throws ParseException {
        Intent notificationIntent = new Intent(activity, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);

        SimpleDateFormat sDFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sDFormat.parse(dateTime));

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(activity, (int) System.currentTimeMillis(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static Notification getNotification(String content, MainActivity activity) {
        Notification.Builder builder = new Notification.Builder(activity)
                .setContentTitle(activity.getString(R.string.app_name))
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher_todo)
                .setLargeIcon(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher_todo));
        return builder.build();
    }
}
