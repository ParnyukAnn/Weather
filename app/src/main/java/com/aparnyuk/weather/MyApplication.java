package com.aparnyuk.weather;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Locale;

public class MyApplication extends Application {
    private SharedPreferences preferences;
    private Locale locale;
    private String lang;

    @Override
    public void onCreate() {
        Log.d("myLogs", "onCreate application - start");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        lang = preferences.getString("lang", "default");
        if (lang.equals("default")) {
            lang = getResources().getConfiguration().locale.getCountry();
        }
        locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, null);
        Log.d("myLogs", "onCreate application - end");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d("myLogs", "onConfigurationChanged application - start");
        super.onConfigurationChanged(newConfig);
        locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, null);
        Log.d("myLogs", "onConfigurationChanged application - end");
    }
}