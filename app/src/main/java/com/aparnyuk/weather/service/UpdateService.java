package com.aparnyuk.weather.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aparnyuk.weather.fragment.MainFragment;
import com.aparnyuk.weather.ModelJR.WeatherInformation;
import com.aparnyuk.weather.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import io.realm.Realm;

public class UpdateService extends IntentService {
    final String TAG = "myLogs";
    // public static final String API_URL = "http://api.openweathermap.org/data/2.5/forecast/city?id=710791&APPID=753b467a7c96ec73dbc7c46ce1b781ba&lang=uk&units=metric";
    public static final String API_URL = "http://api.openweathermap.org/data/2.5/forecast/city?id=";
    public static final String API_CITY_CODE = "710791";
    public static final String API_APPID = "&APPID=753b467a7c96ec73dbc7c46ce1b781ba&lang=";
    public static final String API_LANG = "ru";
    public static final String API_UNITS = "&units=metric";
    private Realm realm;
    SharedPreferences sp;
    SharedPreferences msp;
    public UpdateService() {
        super("UpdateService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "UpdateService - onCreate");
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        msp = getSharedPreferences("com.aparnyuk.weather", Context.MODE_PRIVATE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent localIntent = new Intent(MainFragment.BROADCAST_ACTION);
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            realm = Realm.getInstance(this);
            // Удаление старой базы
            realm.beginTransaction();
            realm.allObjects(WeatherInformation.class).clear();
            realm.commitTransaction();
            // Скачивание данных и запись в базу
            JsonToWeatherInformationList(downloadJSON());
            realm.close();
            localIntent.putExtra(MainFragment.PARAM_STATUS, MainFragment.STATUS_FINISH);
        } else {
            localIntent.putExtra(MainFragment.PARAM_STATUS, MainFragment.STATUS_FAILED);
        }
        localIntent.putExtra(MainFragment.PARAM_RESULT, 10);
        sendBroadcast(localIntent);
    }

    protected String downloadJSON() {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        String jsonStr = null;
        String language = sp.getString("lang", API_LANG);
        String  city = getResources().getStringArray(R.array.city_id)[msp.getInt("saved_city", 0)];
        try {
            String url =   API_URL+city+API_APPID+language+API_UNITS;
            //connection = (HttpURLConnection) new URL(API_URL).openConnection();
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String strLine;
            while ((strLine = bufferedReader.readLine()) != null)
                buffer.append(strLine);
            jsonStr = buffer.toString();
            inputStream.close();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "UpdateService - finish connection, city - " +msp.getInt("saved_city", 0)+" "+city);
        return jsonStr;
    }

    private List<WeatherInformation> JsonToWeatherInformationList(String jsonStr) {
        try {
            Gson gson = new Gson();
            JSONObject jObject = new JSONObject(jsonStr);
            JSONArray list = jObject.getJSONArray("list");
            Log.d(TAG, "UpdateService: " + list.toString());
            for (int i = 0; i < list.length(); i++) {
                JSONObject jObj = list.getJSONObject(i);
                realm.beginTransaction();
                realm.createObjectFromJson(WeatherInformation.class, jObj);
                realm.commitTransaction();
            }
            return realm.allObjects(WeatherInformation.class);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"updateService on Destroy");
        super.onDestroy();
    }
}

