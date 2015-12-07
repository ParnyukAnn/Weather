package com.aparnyuk.weather;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import android.os.AsyncTask;
import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.util.Log;
import android.widget.Toast;

import com.aparnyuk.weather.ModelJR.WeatherInformation;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;


public class MainFragment extends ListFragment {
    private static final String TAG = "myLogs";
    private WeatherAdapter weatherAdapter;
    private Realm realm;

    public interface onItemClickListener {
        public void itemClick(int position, String weather);
    }

    onItemClickListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getInstance(getContext());

        Log.d(TAG, "in MainFragment onCreate");
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if ((savedInstanceState == null) && (netInfo != null && netInfo.isConnectedOrConnecting())) {
            Toast.makeText(getActivity(), "Есть соединение с интернетом!", Toast.LENGTH_LONG).show();
            // Удаление старой базы
            realm.beginTransaction();
            realm.allObjects(WeatherInformation.class).clear();
            realm.commitTransaction();
            // Скачивание данных и запись в базу
            DownloadInfo weatherTask = new DownloadInfo(getActivity());
            weatherTask.execute();
        } else {
            // Загрузка последней инф. из базы если есть
            if (realm.allObjects(WeatherInformation.class).size() != 0) {
                weatherAdapter = new WeatherAdapter(getContext(), realm.allObjects(WeatherInformation.class));
                setListAdapter(weatherAdapter);
                Toast.makeText(getActivity(), "Загрузка данных из БД", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Нет данных в БД", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (onItemClickListener) activity;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.d(TAG, "onListItemClick in MainFragment");
        WeatherInformation wthObj = (WeatherInformation) weatherAdapter.getItem(position);
        String key = wthObj.getDt();
        listener.itemClick(position, key);
    }

    public class DownloadInfo extends AsyncTask<Void, Void, String> {
        public static final String API_URL = "http://api.openweathermap.org/data/2.5/forecast/city?id=710791&APPID=753b467a7c96ec73dbc7c46ce1b781ba&lang=ru&units=metric";
        private Context context;

        public DownloadInfo(Context ctx) {
            context = ctx;
        }


        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection connection = null;
            BufferedReader bufferedReader = null;
            String jsonStr = null;
            Log.d(TAG, "start connection");
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
            return jsonStr;
        }

        private List<WeatherInformation> JsonToWeatherInformationList(String jsonStr) {
            try {
                Gson gson = new Gson();
                JSONObject jObject = new JSONObject(jsonStr);
                JSONArray list = jObject.getJSONArray("list");
                Log.d(TAG, list.toString());
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "Create adapter");
            weatherAdapter = new WeatherAdapter(context, JsonToWeatherInformationList(result));
            setListAdapter(weatherAdapter);
        }
    }
}


