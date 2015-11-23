package com.aparnyuk.weather;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DetailFragment extends Fragment {
    WeatherInformation w = null;

    public static DetailFragment newInstance(int pos, String wth) {
        DetailFragment details = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt("position", pos);
        args.putString("weather", wth);
        details.setArguments(args);
        return details;
    }

    int getPosition() {
        return getArguments().getInt("position", 0);
    }

    String getWeather() {
        return getArguments().getString("weather", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, container, false);

        String weather = getWeather();
        if (weather != null) {
            Gson gson = new Gson();
            w = gson.fromJson(weather, WeatherInformation.class);

        ((TextView) v.findViewById(R.id.tvTimeDetail)).setText((w.dt_txt).substring(11, 16));
        ((TextView) v.findViewById(R.id.tvDataDetail)).setText((w.dt_txt).substring(0, 10));
        String des = w.weather[0].description;
        des = des.substring(0, 1).toUpperCase() + des.substring(1);
        ((TextView) v.findViewById(R.id.tvWeatherDetail)).setText(des);
        if (Math.round(w.main.temp) > 0) {
            ((TextView) v.findViewById(R.id.tvTemperatureDetail)).setText(" +" + Math.round(w.main.temp) + "C");
        } else {
            ((TextView) v.findViewById(R.id.tvTemperatureDetail)).setText(" " + Math.round(w.main.temp) + "C");
        }
        ((TextView) v.findViewById(R.id.tvPressure)).setText("Атм. давл.: " + Math.round(w.main.pressure) + " мм рт.ст.");
        ((TextView) v.findViewById(R.id.tvWindSpeed)).setText("Скорость ветра: " + Math.round(w.wind.speed) + " м/с");
        ((TextView) v.findViewById(R.id.tvWind)).setText("Направление ветра: " + w.wind.deg);
        ((TextView) v.findViewById(R.id.tvHumidity)).setText("Влажность: " + w.main.humidity + " %");

        Picasso.with(getContext())
                .load("http://openweathermap.org/img/w/" + w.weather[0].icon + ".png")
                .placeholder(R.drawable.dunno)
                .error(R.drawable.dunno)
                .into((ImageView) v.findViewById(R.id.ivImageDetail));

        try {
            String dt = w.dt_txt;
            Locale locale = new Locale("ru");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(dt);
            Calendar c = Calendar.getInstance();
            c.set(date.getYear(), date.getMonth(), date.getDay());
             dt = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale);
            dt = dt.substring(0, 1).toUpperCase() + dt.substring(1);
            ((TextView) v.findViewById(R.id.tvDayDetail)).setText(dt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
        return v;
    }


}