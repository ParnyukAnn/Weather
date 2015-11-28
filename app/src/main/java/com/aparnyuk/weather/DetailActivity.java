package com.aparnyuk.weather;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class DetailActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE && isLarge()) {
            finish();
            return;
        }

        if (savedInstanceState == null) {

            DetailFragment details = DetailFragment.newInstance(getIntent().getIntExtra("position", 0),
                    getIntent().getStringExtra("key"));
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
        }
    }

    boolean isLarge() {
        return (getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}