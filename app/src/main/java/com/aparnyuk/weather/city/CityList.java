package com.aparnyuk.weather.city;

import android.content.Context;
import android.util.Log;

import com.aparnyuk.weather.city.CityModel;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CityList {
    ArrayList<CityModel> cities = new ArrayList<>();
    Context ctx;
    public static final String TAG = "myLogs";

    public List<CityModel> getCities() {
        return readJsonStream("UA");
    }

    public CityList(Context context) {
        this.ctx = context;
    }

    public List<CityModel> readJsonStream(String country)  {
        try {
            InputStream is = ctx.getAssets().open("city.json");
            JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
            List<CityModel> cityList = new ArrayList<>();
            Gson gson = new Gson();
            reader.beginArray();
            int i = 0;
            while (reader.hasNext()) {
                CityModel city = gson.fromJson(reader, CityModel.class);
                if (city.getCountry().equals(country)) {
                    cityList.add(city);
                    Log.d(TAG, "citylist: " + i + " " + cityList.get(i).getName());
                    i++;
                }
            }
            reader.endArray();
            reader.close();
            return cityList;
        } catch (IOException e) {
            Log.d(TAG, "error " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
