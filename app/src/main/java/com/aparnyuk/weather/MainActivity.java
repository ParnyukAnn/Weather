package com.aparnyuk.weather;

import com.aparnyuk.weather.MainFragment.onItemClickListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity implements
        onItemClickListener {

    int position = 0;
    String weather = null;
    boolean withDetails = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position");
            weather = savedInstanceState.getString("weather");
        }
        withDetails = (findViewById(R.id.container) != null);
        if (withDetails) {
            showDetails(position, weather);
        }
    }

    void showDetails(int pos, String wth) {
        if (withDetails) {
            DetailFragment details = (DetailFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.container);

            if (details == null || details.getPosition() != pos) {
                details = DetailFragment.newInstance(pos, wth);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, details)
                        .commit();
            }
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("weather", wth);
            startActivity(intent);
        }
    }

    @Override
    public void itemClick(int position, String weather) {
        this.position = position;
        this.weather=weather;
        showDetails(position, weather);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", position);
        outState.putString("weather", weather);

    }
}