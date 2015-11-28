package com.aparnyuk.weather;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aparnyuk.weather.ModelJR.WeatherInformation;
import com.squareup.picasso.Picasso;



public class WeatherAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    List<WeatherInformation> objects;

    public WeatherAdapter(Context context, List<WeatherInformation> arrayWeather) {
        this.ctx = context;
        this.objects = arrayWeather;
        this.lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return objects.size();
    }


    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            v = lInflater.inflate(R.layout.item, parent, false);
        }

        PrintInfo w = new PrintInfo(getWeatherInformation(position));

        ((TextView) v.findViewById(R.id.tvTime)).setText(w.printTime());
        ((TextView) v.findViewById(R.id.tvData)).setText(w.printDate());
        ((TextView) v.findViewById(R.id.tvDay)).setText(w.printDay(true));
        ((TextView) v.findViewById(R.id.tvWeather)).setText(w.printDescription(true));
        ((TextView) v.findViewById(R.id.tvTemperature)).setText(w.printTemp());

        Picasso.with(ctx)
                .load("http://openweathermap.org/img/w/" + w.getIcon() + ".png")
                .placeholder(R.drawable.dunno)
                .error(R.drawable.dunno)
                .into((ImageView) v.findViewById(R.id.ivImage));
        return v;
    }

    private WeatherInformation getWeatherInformation(int position) {
        return ((WeatherInformation) getItem(position));
    }
}