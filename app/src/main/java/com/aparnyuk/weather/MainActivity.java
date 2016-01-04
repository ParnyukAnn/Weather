package com.aparnyuk.weather;

import com.aparnyuk.weather.MainFragment.onItemClickListener;
import com.aparnyuk.weather.adapter.SpinnerAdapter;
import com.aparnyuk.weather.city.CityModel;
import com.aparnyuk.weather.service.NotifyService;
import com.aparnyuk.weather.service.UpdateService;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements onItemClickListener {

    int position = 0;
    String key = null;
    boolean withDetails = true;
    private static final String TAG = "myLogs";
    private List<String> cit = new ArrayList<String>();
    SharedPreferences sp;
    SharedPreferences prefs = null;
    final String SAVED_CITY = "saved_city";
    public static final String APP_PREFERENCES = "weatherSettings";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//!!
        // CityList list = new CityList(this);
        //  List<CityModel> some = list.getCities();
//!!
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //   getSupportActionBar().setLogo(R.mipmap.ic_launcher_2);


        Log.d(TAG, "onCreate in MainActivity, notify don't work - " + NotifyService.state);
        //startService(new Intent(this, NotificationService.class));

        prefs = getSharedPreferences("com.aparnyuk.weather", MODE_PRIVATE);
//!! Add spinner into Toolbar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher_2);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Spinner mSpinner = (Spinner) findViewById(R.id.spinner_city);

        String[] items = getResources().getStringArray(R.array.test_city);
        List<String> spinnerItems = new ArrayList<String>();

        for (int i = 0; i < items.length; i++) {
            spinnerItems.add(items[i]);
        }

        SpinnerAdapter adapter = new SpinnerAdapter(actionBar.getThemedContext(), spinnerItems);
        mSpinner.setAdapter(adapter);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mSpinner.setDropDownVerticalOffset(-116);
        }
//!!

        //!!
        mSpinner.setSelection(loadCity());
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    saveCity(position);
                    Intent localIntent = new Intent(getBaseContext(), UpdateService.class);
                    startService(localIntent);
                } else {
                    Toast.makeText(getBaseContext(), "Sorry.There is no internet connection: your can't see weather for this city.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        //!!
/*
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        if ((NotifyService.state) && (sp.getBoolean("notif", false) == true)) {
            startService(new Intent(this, NotifyService.class));
        }
*/
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position");
            key = savedInstanceState.getString("key");
        }
        withDetails = (findViewById(R.id.container) != null);
        if (withDetails) {
            showDetails(position, key);
        }
    }




    void showDetails(int pos, String k) {
        Log.d(TAG, "showDetails in MainActivity");
        if (withDetails) {
            DetailFragment details = (DetailFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.container);
            if (details == null || details.getPosition() != pos) {
                details = DetailFragment.newInstance(pos, k);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, details)
                        .commit();
            }
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("key", k);
            startActivity(intent);
        }
    }

    @Override
    public void itemClick(int position, String key) {
        Log.d(TAG, "itemClick in MainActivity");
        this.position = position;
        this.key = key;
        showDetails(position, key);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "saveInstanceState in MainActivity");
        outState.putInt("position", position);
        outState.putString("key", key);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //     MenuItem mi = menu.add(0, 1, 0, "Preferences");
        //     mi.setIntent(new Intent(this, PrefActivity.class));
        return true;//super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, PrefActivity.class);
            try {
                startActivity(intent);
            } catch (Exception e) {
                Log.d(TAG, "exept + " + e);
            }
            return true;
        }
        if (id == R.id.action_refresh) {
            Intent localIntent = new Intent(this, UpdateService.class);
            startService(localIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveCity(int num) {
        SharedPreferences sp = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        // sp = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(SAVED_CITY, num);
        ed.apply();
        Toast.makeText(this, "City saved", Toast.LENGTH_SHORT).show();
    }

    public int loadCity() {
        SharedPreferences sp = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        //sp = getPreferences(MODE_PRIVATE);
        int savedNum = sp.getInt(SAVED_CITY, 0);
        Toast.makeText(this, "Load city, city code: " + savedNum, Toast.LENGTH_SHORT).show();
        return savedNum;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.getBoolean("firstrun", true)) {
            // При первом запуске (или если юзер удалял все данные приложения)
            // мы попадаем сюда.
            //пытаемся получить значение
            Boolean notif = prefs.getBoolean("notif", false);
            Log.d(TAG, "in MainActivity, onResume - notification state: " + notif);
            if (!notif) {
                notif = true;
                SharedPreferences.Editor editor = prefs.edit();
                //устанавливаем дефолтное значение
                editor.putBoolean("notif", true);
                editor.commit();
                Log.d(TAG, "in MainActivity, onResume - notification state before start service: " + notif);
                startService(new Intent(this, NotifyService.class));
            }
            Toast.makeText(this, "First run", Toast.LENGTH_SHORT).show();
            prefs.edit().putBoolean("firstrun", false).commit();

            String lang = prefs.getString("lang","default");
            if (!lang.equals("ru")) {
                SharedPreferences.Editor editor = prefs.edit();
                //устанавливаем дефолтное значение
                editor.putString("lang", "ru");
                editor.commit();
                Log.d(TAG, "in MainActivity, onResume - notification state before start service: " + notif);
            }
        }
    }
}