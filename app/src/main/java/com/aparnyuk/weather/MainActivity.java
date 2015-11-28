package com.aparnyuk.weather;

import com.aparnyuk.weather.MainFragment.onItemClickListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import io.realm.Realm;

public class MainActivity extends FragmentActivity implements
        onItemClickListener {

    int position = 0;
    String key = null;
    boolean withDetails = true;
    private static final String TAG = "myLogs";

    Realm realm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
//!!
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
}