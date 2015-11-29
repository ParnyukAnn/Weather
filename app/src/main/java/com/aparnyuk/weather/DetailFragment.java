package com.aparnyuk.weather;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aparnyuk.weather.ModelJR.WeatherInformation;
import com.squareup.picasso.Picasso;

import io.realm.Realm;


public class DetailFragment extends Fragment {
    WeatherInformation wi = null;
    private static final String TAG = "myLogs";
    Realm realm;

    public static DetailFragment newInstance(int pos, String k) {
        DetailFragment details = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt("position", pos);
        args.putString("key", k);
        details.setArguments(args);
        return details;
    }

    int getPosition() {
        return getArguments().getInt("position", 0);
    }

    String getKey() {
        return getArguments().getString("key", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        realm = Realm.getInstance(getContext());
        String key = getKey();
        if (key != null) {
            wi = realm.where(WeatherInformation.class).equalTo("dt", key).findFirst();
            showDetailInfo(v);
        }
        return v;
    }

    private void showDetailInfo(View v) {
        PrintInfo w = new PrintInfo(wi);
        Log.d(TAG, "Detail fragment showDetailInfo");

        ((TextView) v.findViewById(R.id.tvTimeDetail)).setText(w.printTime());
        ((TextView) v.findViewById(R.id.tvDataDetail)).setText(w.printDate());
        ((TextView) v.findViewById(R.id.tvDayDetail)).setText(w.printDay(false));
        ((TextView) v.findViewById(R.id.tvWeatherDetail)).setText(w.printDescription(false));
        ((TextView) v.findViewById(R.id.tvTemperatureDetail)).setText(w.printTemp());
        ((TextView) v.findViewById(R.id.tvPressure)).setText(w.printPressure());
        ((TextView) v.findViewById(R.id.tvWindSpeed)).setText(w.printWindSpeed());
        ((TextView) v.findViewById(R.id.tvWind)).setText(w.printWindDirection());
        ((TextView) v.findViewById(R.id.tvHumidity)).setText(w.printHumidity());

        Picasso.with(getContext())
                .load(picResource(wi.getWeather().get(0).getId(), w.isNight()))
            //    .placeholder(R.drawable.dunno)
            //    .error(R.drawable.dunno)
                .into((ImageView) v.findViewById(R.id.ivImageDetail));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private int picResource(int code, boolean isNight) {
        int res = R.drawable.dunno;
        if (isNight) {
            if ((code >= 200) && (code < 300)) {
                res = R.drawable.p11n;
            } else if (((code >= 300) && (code < 400)) || ((code >= 520) && (code <= 531))) {
                res = R.drawable.p09n;
            } else if ((code >= 500) && (code <= 504)) {
                res = R.drawable.p10n;
            } else if ((code == 511) || ((code >= 600) && (code < 700))) {
                res = R.drawable.p13n;
            } else if ((code >= 700) && (code < 800)) {
                res = R.drawable.p50n;
            } else if (code == 800) {
                res = R.drawable.p01n;
            } else if (code == 801) {
                res = R.drawable.p02n;
            } else if (code == 802) {
                res = R.drawable.p03n;
            } else if ((code == 803) || (code == 804)) {
                res = R.drawable.p04n;
            } else {
                res = R.drawable.dunno;
            }
        } else {
            if ((code >= 200) && (code < 300)) {
                res = R.drawable.p11d;
            } else if (((code >= 300) && (code < 400)) || ((code >= 520) && (code <= 531))) {
                res = R.drawable.p09d;
            } else if ((code >= 500) && (code <= 504)) {
                res = R.drawable.p10d;
            } else if ((code == 511) || ((code >= 600) && (code < 700))) {
                res = R.drawable.p13d;
            } else if ((code >= 700) && (code < 800)) {
                res = R.drawable.p50d;
            } else if (code == 800) {
                res = R.drawable.p01d;
            } else if (code == 801) {
                res = R.drawable.p02d;
            } else if (code == 802) {
                res = R.drawable.p03d;
            } else if ((code == 803) || (code == 804)) {
                res = R.drawable.p04d;
            } else {
                res = R.drawable.dunno;
            }
        }
        return res;
    }
}