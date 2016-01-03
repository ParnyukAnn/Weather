package com.aparnyuk.weather.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.IBinder;

import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.aparnyuk.weather.MainActivity;
import com.aparnyuk.weather.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class NotifyService extends Service {

    private NotificationManager nm;
    private final int NOTIFICATION_ID = 73;
    public static boolean state = true;
    Timer mTimer;
  //  SharedPreferences sp;

    public NotifyService() {
    }

    final String TAG = "myLogs";

    public void onCreate() {
        super.onCreate();
        state = false;
        nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d(TAG, "NotifyService - onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "NotifyService - onStartCommand");
       // sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mTimer = new Timer();
        MyTimerTask mMyTimerTask = new MyTimerTask();
        //mTimer.schedule(mMyTimerTask, 1000, sp.getLong("freq", 60000));
        mTimer.schedule(mMyTimerTask, 1000, 60000);
        return Service.START_STICKY;
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
            Intent appIntent = new Intent(getApplicationContext(), MainActivity.class);
            Intent downloadIntent = new Intent(getApplicationContext(), UpdateService.class);
            PendingIntent appPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent downloadPendingIntent = PendingIntent.getService(getApplicationContext(), 0, downloadIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder
                    .setContentIntent(appPendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher_2)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                    .setTicker(getString(R.string.notification_ticker))
                    .setWhen(System.currentTimeMillis()) // время уведомления - текущее
                    .setAutoCancel(true) // для автоматического закрытия
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.notification_text))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.notification_long_text)))
                    .addAction(R.drawable.ic_refresh_white_24dp, getString(R.string.notification_update_button), downloadPendingIntent);


            //notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

            Notification notification = builder.build();
/*
            switch (sp.getInt("sound_type", 2)) {
                case 1:
                    notification.defaults = Notification.DEFAULT_SOUND;
                    break;
                case 2:
                    break;
                case 3:
                    notification.defaults = Notification.DEFAULT_VIBRATE;
                    break;

            }

*/
            nm.notify(NOTIFICATION_ID, notification);
            Log.d(TAG, "Notification in TimerTask");
/*          runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
            */
        }
    }

    public void onDestroy() {
        Log.d(TAG, "NotifyService - onDestroy");
        nm.cancel(NOTIFICATION_ID);
        mTimer.cancel();
        super.onDestroy();
        state = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "NotifyService - onBind");
        throw new UnsupportedOperationException("Not yet implemented");
    }
}