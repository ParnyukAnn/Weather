package com.aparnyuk.weather;

import android.app.Activity;
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
import java.util.ArrayList;
import android.util.Log;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainFragment extends ListFragment {
    private static final String TAG = "myLogs";
    private WeatherAdapter weatherAdapter;

    public interface onItemClickListener {
        public void itemClick(int position,String weather);
    }

    onItemClickListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "in MainFragment onCreate");
        DownloadInfo weatherTask = new DownloadInfo(getActivity());
        weatherTask.execute();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (onItemClickListener) activity;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Gson gson = new Gson();
        String weather = gson.toJson(weatherAdapter.getItem(position));

        listener.itemClick(position,weather);
    }


    public class DownloadInfo extends AsyncTask<Void, Void, ArrayList<WeatherInformation>> {
        public static final String API_URL = "http://api.openweathermap.org/data/2.5/forecast/city?id=710791&APPID=753b467a7c96ec73dbc7c46ce1b781ba&lang=ru&units=metric";
        private Context context;

        public DownloadInfo(Context ctx) {
            context = ctx;
        }


        @Override
        protected ArrayList<WeatherInformation> doInBackground(Void... params) {

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
            return JsonToWeatherInformationList(jsonStr);
        }

        private ArrayList<WeatherInformation> JsonToWeatherInformationList(String jsonStr) {
            try {
                Gson gson = new Gson();
                JSONObject jObject = new JSONObject(jsonStr);
                ArrayList<WeatherInformation> wList = new ArrayList<>();
                JSONArray list = jObject.getJSONArray("list");
                for (int i = 0; i < list.length(); i++) {
                    JSONObject jObj = list.getJSONObject(i);
                    wList.add(gson.fromJson(jObj.toString(), WeatherInformation.class));
                }
                Log.d(TAG, "OK");
                return wList;
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
// !!! ВОПРОС: есть ли корректная передача даных (result) из потока?
        @Override
        protected void onPostExecute(ArrayList<WeatherInformation> result) {
            Log.d(TAG, "Create adapter");
            /*WeatherAdapter*/ weatherAdapter = new WeatherAdapter(context, result);
            setListAdapter (weatherAdapter);
            Log.d(TAG, "Create adapter - OK");
            super.onPostExecute(result);
        }
    }
}


