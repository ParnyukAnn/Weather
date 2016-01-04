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

    public static final String TAG = "Logs";
    final int DIALOG_EXIT = 1;
    final int DIALOG_REFRESH = 2;
    final int DIALOG_NO_INT = 3;
    PendingIntent intent;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Log.d(TAG, "in Pref on create - before lang");
        // Cмена языка приложения
        Preference lang = (Preference) findPreference("lang");
        lang.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object something) {
                Log.d(TAG, "in Pref change lang");
                if (isNetworkAvailable(getBaseContext())) {
                    showDialog(DIALOG_EXIT);
                } else {
                    showDialog(DIALOG_NO_INT);
                }

                return true;
            }
        });

        Log.d(TAG, "in Pref on create- before reset");

        Preference reset = (Preference) findPreference("reset");
        reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(TAG, "reset - before check int");
                if (isNetworkAvailable(getBaseContext())) {
                    Log.d(TAG, "reset - is int");
                    sp.edit().clear().commit();
                    //startActivity(new Intent(getApplicationContext(), PrefActivity.class));
                    // SwitchPreference notif = (SwitchPreference) findPreference("notif");
                    // notif.setChecked(true);
                    restartApp();
                } else {
                    Log.d(TAG, "reset - no int");
                    showDialog(DIALOG_REFRESH);
                }
                return true;
            }
        });

        // Включение-отключение уведомлений
        Preference notif = (Preference) findPreference("notif");
        //notif.setEnabled(true);
        notif.getOnPreferenceChangeListener();
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

        Preference notif_freq = (Preference) findPreference("frequency");
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
    }

    protected Dialog onCreateDialog(int id) {
        Log.d(TAG, "in dialog - before check int");
            if (id == DIALOG_EXIT) {
                Log.d(TAG, "for lang - is int");
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle(R.string.exit_dialog_title);
                adb.setMessage(R.string.exit_dialog_message);
                adb.setIcon(android.R.drawable.ic_dialog_info);
                adb.setPositiveButton(R.string.exit_dialog_yes, myClickListener);
                adb.setNeutralButton(R.string.exit_dialog_cancel, myClickListener);
                return adb.create();
            }
            if (id == DIALOG_NO_INT) {
                Log.d(TAG, "for lang - no int");
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle(R.string.network_dialog_title);
                adb.setMessage(R.string.network_dialog_message);
                adb.setIcon(android.R.drawable.ic_dialog_info);
                adb.setNeutralButton(R.string.network_dialog_cancel, myClickListener);
                return adb.create();
            }
            if (id == DIALOG_REFRESH) {
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle(R.string.network_dialog_title);
                adb.setMessage(R.string.network_dialog_message_for_refresh);
                adb.setIcon(android.R.drawable.ic_dialog_info);
                adb.setNeutralButton(R.string.network_dialog_cancel, myClickListener);
                return adb.create();
            }
        return super.onCreateDialog(id);
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    restartApp();
                    finish();
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    finish();
                    break;
                //case Dialog.BUTTON_:
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

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();//isConnectedOrConnecting()
    }
}
