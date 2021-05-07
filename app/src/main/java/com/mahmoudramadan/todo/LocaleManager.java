package com.mahmoudramadan.todo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

class LocaleManager {

    public static void setLocale(Activity activity, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(config, activity.getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = activity.getSharedPreferences("Settings", activity.MODE_PRIVATE).edit();
        editor.putString("lang", language);
        editor.apply();
    }

    public static void loadLocale(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences("Settings", activity.MODE_PRIVATE);
        String language = preferences.getString("lang", "");
        setLocale(activity, language);
    }
}
