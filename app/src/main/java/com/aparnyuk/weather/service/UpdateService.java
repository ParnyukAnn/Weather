package com.aparnyuk.weather.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.aparnyuk.weather.MainFragment;
import com.aparnyuk.weather.ModelJR.WeatherInformation;
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
    public static final String API_URL = "http://api.openweathermap.org/data/2.5/forecast/city?id=710791&APPID=753b467a7c96ec73dbc7c46ce1b781ba&lang=ru&units=metric";
    private Realm realm;

    public UpdateService() {
        super("UpdateService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "UpdateService - onCreate");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "UpdateService - onHandleIntent");

        Intent localIntent = new Intent(MainFragment.BROADCAST_ACTION);

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            realm = Realm.getInstance(this);
            // Toast.makeText(this, "Сервис обновил данные", Toast.LENGTH_LONG).show();
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

        //LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    protected String downloadJSON() {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        String jsonStr = null;
        Log.d(TAG, "UpdateService - start connection");
        try {
            connection = (HttpURLConnection) new URL(API_URL).openConnection();
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
        Log.d(TAG, "UpdateService - finish connection");
        return jsonStr;
    }

    private List<WeatherInformation> JsonToWeatherInformationList(String jsonStr) {
        Log.d(TAG, "UpdateService - from json to list");
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
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }
        Log.d(TAG, "NULL");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "UpdateService - onDestroy");
    }
}

