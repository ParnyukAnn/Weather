package com.aparnyuk.weather;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "myLogs";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Log.d(TAG, "onCreate in DetailActivity");
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE && isLarge()) {
            finish();
            return;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            DetailFragment details = DetailFragment.newInstance(getIntent().getIntExtra("position", 0),
                    getIntent().getStringExtra("key"));
            getSupportFragmentManager().beginTransaction().add(R.id.cont, details).commit();
        }
    }

    boolean isLarge() {
        return (getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateMenu in DetailActivity");
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        Log.d(TAG, "onCreateMenu in DetailActivity");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.detail_action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}