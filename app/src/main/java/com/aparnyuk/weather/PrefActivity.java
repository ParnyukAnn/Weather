package com.aparnyuk.weather;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.Switch;

import com.aparnyuk.weather.service.NotifyService;
import com.aparnyuk.weather.service.UpdateService;

public class PrefActivity extends PreferenceActivity {
    PendingIntent intent;
    SharedPreferences sp;
    final int DIALOG_EXIT = 1;
    public static final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
/*
        Log.d(TAG, "in Pref on create");
        intent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getIntent()), 0);
        Log.d(TAG, "in Pref on create - before lang");
        Preference lang = (Preference) findPreference("lang");
        lang.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object something) {
                // вызываем диалог
                showDialog(DIALOG_EXIT);
                return true;
            }
        });
        // получаем SharedPreferences, которое работает с файлом настроек
        Log.d(TAG, "in Pref on create- before reset");
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // Полная очистка настроек
        Preference reset = (Preference) findPreference("reset");
        reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //
                sp.edit().clear().commit();
                //startActivity(new Intent(getApplicationContext(), PrefActivity.class));
                SwitchPreference notif = (SwitchPreference) findPreference("notif");
                notif.setChecked(true);

                restartApp();

                return true;
            }
        });
        Log.d(TAG, "in Pref on create - bfore notif ");
        // Включение-отключение уведомлений
        Preference notif = (Preference) findPreference("notif");
        //notif.setEnabled(true);
        notif.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object something) {
                        if ((NotifyService.state) && (!sp.getBoolean("notif", false))) {
                            startService(new Intent(getApplicationContext(), NotifyService.class));
                        }
                        if ((!NotifyService.state) && (sp.getBoolean("notif", false))) {
                            stopService(new Intent(getApplicationContext(), NotifyService.class));
                        }
                        return true;
                    }
                }
        );
        Log.d(TAG, "in Pref on create - bfore notif sound");
        Preference sound = (Preference) findPreference("sound");
        sound.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object something) {

                if (!NotifyService.state) {
                    stopService(new Intent(getApplicationContext(), NotifyService.class));
                }
                startService(new Intent(getApplicationContext(), NotifyService.class));
                return true;
            }
        });
        Log.d(TAG, "in Pref on create - bfore notif freq");
        Preference notif_freq = (Preference) findPreference("freq");
        notif_freq.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object something) {
                if (!NotifyService.state) {
                    stopService(new Intent(getApplicationContext(), NotifyService.class));
                }
                startService(new Intent(getApplicationContext(), NotifyService.class));
                return true;
            }
        });
        Log.d(TAG, "in Pref on create - after notif freq");*/
    }

    protected Dialog onCreateDialog(int id) {

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            if (id == DIALOG_EXIT) {
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle(R.string.exit_dialog_title);
                adb.setMessage(R.string.exit_dialog_message);
                adb.setIcon(android.R.drawable.ic_dialog_info);
                adb.setPositiveButton(R.string.exit_dialog_yes, myClickListener);
                adb.setNeutralButton(R.string.exit_dialog_cancel, myClickListener);
                return adb.create();
            }
        } else {
            if (id == DIALOG_EXIT) {
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle(R.string.network_dialog_title);
                adb.setMessage(R.string.network_dialog_message);
                adb.setIcon(android.R.drawable.ic_dialog_info);
                adb.setNeutralButton(R.string.network_dialog_cancel, myClickListener);
                return adb.create();
            }
        }
        return super.onCreateDialog(id);
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // положительная кнопка
                case Dialog.BUTTON_POSITIVE:
                    restartApp();
                    finish();
                    break;
                // негаитвная кнопка
                case Dialog.BUTTON_NEGATIVE:
                    finish();
                    break;
            }
        }
    };

    private void restartApp() {
        Intent localIntent = new Intent(getApplication(), UpdateService.class);
        getApplication().startService(localIntent);

        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, intent);
        System.exit(1);
        // перезапуск приложения
        /*Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);*/
    }
}
//stopService(new Intent(this, MyService.class));