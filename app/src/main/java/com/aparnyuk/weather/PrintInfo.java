package com.aparnyuk.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;

import com.aparnyuk.weather.ModelJR.WeatherInformation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class PrintInfo {
    private WeatherInformation w;
    private Context c;
    SharedPreferences sp;

    public PrintInfo(Context context, WeatherInformation weather) {
        w = weather;
        c = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String printTemp() {
        String temp = "";
        if (Math.round(w.getMain().getTemp()) > 0) {
            temp = " +" + Math.round(w.getMain().getTemp()) + c.getString(R.string.describe_temperature_celsius);
        } else {
            temp = " " + Math.round(w.getMain().getTemp()) + c.getString(R.string.describe_temperature_celsius);
        }
        return temp;
    }

    public String printPressure() {
        return c.getString(R.string.describe_pressure_begin)+" " + Math.round(w.getMain().getPressure() * 0.7500637) +" " + c.getString(R.string.describe_pressure_end);

    }

    public String printHumidity() {
        return c.getString(R.string.describe_humidity_begin)+" " + w.getMain().getHumidity()+" "  + c.getString(R.string.describe_humidity_end) ;
    }

    //!!! Дополнить описание погоды (дощ, тучи..)
    public String printDescription(boolean simpleFormat) {
        String des = w.getWeather().get(0).getDescription();
        if (!simpleFormat) {
            des = des.substring(0, 1).toUpperCase() + des.substring(1);
        }
        return des;
    }

    public String getIcon() {
        return w.getWeather().get(0).getIcon();
    }

    public String printWindSpeed() {
        return c.getString(R.string.describe_wind_speed_begin)+" " + Math.round(w.getWind().getSpeed())+" "  + c.getString(R.string.describe_wind_speed_end);
    }

    public String printWindDirection() {
        int d = Math.round(w.getWind().getDeg());
        String direct = "";
        if (((d >= 0) && (d < 23)) || ((d >= 338) && (d <= 360))) {
            direct = c.getString(R.string.describe_wind_direction_northern);  // (0° и 360°)
        } else if ((d >= 23) && (d < 68)) {
            direct = c.getString(R.string.describe_wind_direction_northeastern); //	45°
        } else if ((d >= 68) && (d < 113)) {
            direct = c.getString(R.string.describe_wind_direction_eastern); // 90°)
        } else if ((d >= 113) && (d < 158)) {
            direct = c.getString(R.string.describe_wind_direction_southeastern); // 135°
        } else if ((d >= 158) && (d < 203)) {
            direct = c.getString(R.string.describe_wind_direction_southern); // 180°
        } else if ((d >= 203) && (d < 248)) {
            direct = c.getString(R.string.describe_wind_direction_southwest); //225°
        } else if ((d >= 248) && (d < 273)) {
            direct = c.getString(R.string.describe_wind_direction_west); // 270°
        } else if ((d >= 273) && (d < 338)) {
            direct = c.getString(R.string.describe_wind_direction_northwestern); // 315°
        }
        direct = c.getString(R.string.describe_wind_direction_begin)+" "+ direct + c.getString(R.string.describe_wind_direction_middle) +" "+ d + c.getString(R.string.describe_wind_direction_end);
        return direct;
    }


    public int showWindDirection() {
        int d = Math.round(w.getWind().getDeg());
        int direct = 0;
        if (((d >= 0) && (d < 23)) || ((d >= 338) && (d <= 360))) {
            direct = R.drawable.ic_arrow_up_grey600_24dp;        // северный (0° и 360°)
        } else if ((d >= 23) && (d < 68)) {
            direct = R.drawable.ic_arrow_top_right_grey600_24dp; // северо-восточный	45°
        } else if ((d >= 68) && (d < 113)) {
            direct = R.drawable.ic_arrow_right_grey600_24dp; // восточный 90°
        } else if ((d >= 113) && (d < 158)) {
            direct = R.drawable.ic_arrow_bottom_right_grey600_24dp; // юго-восточный" 135°
        } else if ((d >= 158) && (d < 203)) {
            direct = R.drawable.ic_arrow_down_grey600_24dp; // западый 180°
        } else if ((d >= 203) && (d < 248)) {
            direct = R.drawable.ic_arrow_bottom_left_grey600_24dp; // юго-западый 225°
        } else if ((d >= 248) && (d < 273)) {
            direct = R.drawable.ic_arrow_left_grey600_24dp; // 270°
        } else if ((d >= 273) && (d < 338)) {
            direct = R.drawable.ic_arrow_top_left_grey600_24dp; // северо-западный" 315°
        }
        return direct;
    }

// Поворот стрелки. Некрасиво отображается.
/*
    public BitmapDrawable showWindDirection(Context c) {
        int d = Math.round(w.getWind().getDeg());
        BitmapDrawable direct = RotateBitmap (c,0);
        if (((d >= 0) && (d < 23)) || ((d >= 338) && (d <= 360))) {
            direct = RotateBitmap (c,0);
        } else if ((d >= 23) && (d < 68)) {
            direct = RotateBitmap (c,45);
        } else if ((d >= 68) && (d < 113)) {
            direct = RotateBitmap (c,90);
        } else if ((d >= 113) && (d < 158)) {
            direct = RotateBitmap (c,135);
        } else if ((d >= 158) && (d < 203)) {
            direct = RotateBitmap (c,180);
        } else if ((d >= 203) && (d < 248)) {
            direct = RotateBitmap (c,225);
        } else if ((d >= 248) && (d < 273)) {
            direct = RotateBitmap (c,270);
        } else if ((d >= 273) && (d < 338)) {
            direct = RotateBitmap (c,315);
        }
        return direct;
    }

    public BitmapDrawable RotateBitmap(Context cont, int angle) {
        Bitmap source = BitmapFactory.decodeResource(cont.getResources(), R.drawable.ic_arrow_up_grey600_24dp);
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap rotateBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        BitmapDrawable bmd = new BitmapDrawable(rotateBitmap);
        return bmd;
    }
*/

    private Date thisDate() {
        Date myDate = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            myDate = format.parse(w.getDt_txt());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myDate;
    }

    public String printDate() {
       // Locale locale = new Locale("ru", "RU");
        //Locale locale = new Locale("en", "EN");
        Locale locale = new Locale(sp.getString("lang", "ru"), sp.getString("lang", "ru").toUpperCase());
        SimpleDateFormat f = new SimpleDateFormat("dd MMMM", locale);
        return f.format(thisDate());
    }

    public String printTime() {
        SimpleDateFormat f = new SimpleDateFormat("HH:mm");
        return f.format(thisDate());
    }

    public String printDay(boolean shortForm) {
        //Locale locale = new Locale("ru", "RU");
        //Locale locale = new Locale("en", "EN");
        Locale locale = new Locale(sp.getString("lang", "ru"), sp.getString("lang", "ru").toUpperCase());
        Calendar cal = new GregorianCalendar();
        cal.setTime(thisDate());
        String day;
        if (shortForm) {
            day = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, locale);
            day = day.substring(0, 1).toUpperCase() + day.substring(1) + c.getString(R.string.describe_point);
        } else {
            day = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale);
            day = day.substring(0, 1).toUpperCase() + day.substring(1);
        }
        return day;
    }

    public boolean isNight() {
        int hours = thisDate().getHours();
        if ((hours >= 6) && (hours <= 18)) {
            return false;
        } else {
            return true;
        }
    }
}
