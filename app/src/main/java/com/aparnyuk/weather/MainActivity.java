package com.aparnyuk.weather;

import com.aparnyuk.weather.MainFragment.onItemClickListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity
        implements onItemClickListener {

    int position = 0;
    String key = null;
    boolean withDetails = true;
    private static final String TAG = "myLogs";

    Realm realm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_2);

        Log.d(TAG, "onCreate in MainActivity");

        realm = Realm.getInstance(this);

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_refresh) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}