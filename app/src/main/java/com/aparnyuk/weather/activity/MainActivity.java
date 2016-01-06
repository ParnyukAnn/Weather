package com.aparnyuk.weather.activity;

import com.aparnyuk.weather.fragment.DetailFragment;
import com.aparnyuk.weather.fragment.MainFragment;
import com.aparnyuk.weather.R;
import com.aparnyuk.weather.adapter.SpinnerAdapter;
import com.aparnyuk.weather.service.NotifyService;
import com.aparnyuk.weather.service.UpdateService;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
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
        implements MainFragment.onItemClickListener {

    private int position = 0;
    private String key = null;
    private boolean withDetails = true;
    private static final String TAG = "myLogs";
    private SharedPreferences prefs = null;
    final String SAVED_CITY = "saved_city";
    public static final String APP_PREFERENCES = "com.aparnyuk.weather";
    Spinner mSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setLogo(R.mipmap.ic_launcher_2);

        Log.d(TAG, "onCreate in MainActivity");

        prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        addSpinner();
        mSpinner.setSelection(loadCity());
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position");
            key = savedInstanceState.getString("key");
        }
        withDetails = (findViewById(R.id.container) != null);
        if (withDetails) {
            showDetails(position, key);
        }
    }

    // Add spinner into Toolbar
    private void addSpinner() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher_2);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mSpinner = (Spinner) findViewById(R.id.spinner_city);

        String[] items = getResources().getStringArray(R.array.test_city);
        List<String> spinnerItems = new ArrayList<>();

        for (int i = 0; i < items.length; i++) {
            spinnerItems.add(items[i]);
        }

        SpinnerAdapter adapter = new SpinnerAdapter(actionBar.getThemedContext(), spinnerItems);
        mSpinner.setAdapter(adapter);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mSpinner.setDropDownVerticalOffset(-116);
        }
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                int pos = loadCity();
                if (position != pos) {
                    if (isNetworkAvailable(getBaseContext())) {
                        saveCity(position);
                        Intent localIntent = new Intent(getBaseContext(), UpdateService.class);
                        startService(localIntent);
                    } else {
                        Toast.makeText(getBaseContext(), getString(R.string.change_city_wihout_internet), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }


    void showDetails(int pos, String k) {
        // Log.d(TAG, "showDetails in MainActivity");
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
        // Log.d(TAG, "itemClick in MainActivity");
        this.position = position;
        this.key = key;
        showDetails(position, key);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Log.d(TAG, "saveInstanceState in MainActivity");
        outState.putInt("position", position);
        outState.putString("key", key);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, PrefActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_refresh) {
            Log.d(TAG, "in MainActivity - onOptionsItemSelected call UpdateService");
            Intent localIntent = new Intent(this, UpdateService.class);
            startService(localIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Saved and loading city in the preferences
    public void saveCity(int num) {
        prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putInt(SAVED_CITY, num);
        ed.apply();
    }

    public int loadCity() {
        prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return prefs.getInt(SAVED_CITY, 0);
    }

    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    // The first launch of the application
    @Override
    protected void onResume() {
        super.onResume();
        if (prefs.getBoolean("firstrun", true)) {

            String lang = prefs.getString("lang", "default");
            if (!lang.equals("ru")) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("lang", "ru");
                editor.commit();
            }

            Boolean notif = prefs.getBoolean("notif", false);
            if (!notif) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("notif", true);
                editor.commit();
                startService(new Intent(this, NotifyService.class));
            }
            prefs.edit().putBoolean("firstrun", false).apply();

            mSpinner.setSelection(21);
        }
    }
}