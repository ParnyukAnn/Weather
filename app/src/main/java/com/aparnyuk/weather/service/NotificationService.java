package com.aparnyuk.weather.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.aparnyuk.weather.MainActivity;
import com.aparnyuk.weather.R;

import java.util.concurrent.TimeUnit;


public class NotificationService extends IntentService {

    final String TAG = "myLogs";

    private NotificationManager nm;
    private final int NOTIFICATION_ID = 73;


    public NotificationService() {
        super("UpdateService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d(TAG, "NotificationService - onCreate");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//!!
        int i = 0;
        while (i < 10) {
            //while (true) {
//!!
            Notification.Builder builder = new Notification.Builder(getApplicationContext());
            Intent appIntent = new Intent(getApplicationContext(), MainActivity.class);
            Intent downloadIntent = new Intent(getApplicationContext(), UpdateService.class);
            PendingIntent appPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent downloadPendingIntent = PendingIntent.getService(getApplicationContext(), 0, downloadIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder
                    .setContentIntent(appPendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher_2)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                    .setTicker("Дступны новые обновления погоды")
                    .setWhen(System.currentTimeMillis()) // время уведомления - текущее
                    .setAutoCancel(true) // для автоматического закрытия
                    .setContentTitle("Sun&Rain")
                    .setContentText("Нажмите чтобы обновить информацию о погоде")
                    .addAction(R.drawable.ic_refresh_white_24dp, "Обновить", downloadPendingIntent);
            Notification notification = builder.build();
            nm.notify(NOTIFICATION_ID, notification);
//!!
            int time = 60;
            // int time = intent.getIntExtra("time", 0);
//!!
            Log.d(TAG, "NotificationService - onHandleIntent start Notification " + i);
            try {
                TimeUnit.SECONDS.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
    }

    @Override
    public void onDestroy() {
        nm.cancel(NOTIFICATION_ID);
        Log.d(TAG, "NotificationService - onDestroy");
        super.onDestroy();
    }
}
