package com.aparnyuk.weather.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import android.content.Context;

import android.util.Log;
import android.widget.Toast;

import com.aparnyuk.weather.ModelJR.WeatherInformation;
import com.aparnyuk.weather.adapter.WeatherAdapter;
import com.aparnyuk.weather.service.UpdateService;

import io.realm.Realm;

public class MainFragment extends ListFragment {
    private static final String TAG = "myLogs";
    private WeatherAdapter weatherAdapter;
    private Realm realm;
    public final static int STATUS_FINISH = 200;
    public final static int STATUS_FAILED = 100;
    public final static String PARAM_RESULT = "result";
    public final static String PARAM_STATUS = "status";
    public final static String BROADCAST_ACTION = "com.aparnyuk.receiver.BROADCAST";

    BroadcastReceiver br;
    SharedPreferences sp;

    public interface onItemClickListener {
        public void itemClick(int position, String weather);
    }

    onItemClickListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getInstance(getContext());
        Log.d(TAG, "in MainFragment onCreate");
        createBroadcast();
        if (savedInstanceState == null) {
            Log.d(TAG,"in MainFragment onCreate UpdateService");
            Intent localIntent = new Intent(getActivity(), UpdateService.class);
            getActivity().startService(localIntent);
        }else {
            downloadFromDB();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (onItemClickListener) activity;
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
        getActivity().unregisterReceiver(br);
    }

    private void createBroadcast() {
        br = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(PARAM_STATUS, 0);
                // int result = intent.getIntExtra(PARAM_RESULT, -1);
                if (status == STATUS_FINISH) {
                    Toast.makeText(getContext(), "Сервис обновил данные.", Toast.LENGTH_LONG).show();
                }
                if (status == STATUS_FAILED) {
                    Toast.makeText(getContext(), "Нет соединения с Интернетом.", Toast.LENGTH_LONG).show();
                }
                downloadFromDB();
            }
        };
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        getActivity().registerReceiver(br, intFilt);
    }

    private void downloadFromDB() {
        if (realm.allObjects(WeatherInformation.class).size() != 0) {
            weatherAdapter = new WeatherAdapter(getContext(), realm.allObjects(WeatherInformation.class));
            setListAdapter(weatherAdapter);
            Toast.makeText(getActivity(), "Загрузка данных из БД", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Нет данных в БД", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        WeatherInformation wthObj = (WeatherInformation) weatherAdapter.getItem(position);
        String key = wthObj.getDt();
        listener.itemClick(position, key);
    }
}


