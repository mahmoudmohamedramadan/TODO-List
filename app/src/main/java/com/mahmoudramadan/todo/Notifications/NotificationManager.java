package com.mahmoudramadan.todo.Notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.mahmoudramadan.todo.MainActivity;
import com.mahmoudramadan.todo.R;

import java.util.Calendar;

public class NotificationManager {

    public static void scheduleNotification(Notification notification, MainActivity activity) {
        Intent notificationIntent = new Intent(activity, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);

        Calendar calendar = Calendar.getInstance();
        // Note: Months value is MonthNumber-1 (Jan is 0, Feb is 1 and so on).
        calendar.set(2021, 4, 10, 13, 6, 0);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(activity, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static Notification getNotification(String content, MainActivity activity) {
        Notification.Builder builder = new Notification.Builder(activity);
        builder.setContentTitle(activity.getString(R.string.app_name));
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_baseline_notifications);
        return builder.build();
    }
}
