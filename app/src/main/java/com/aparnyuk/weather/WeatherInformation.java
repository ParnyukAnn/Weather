package com.aparnyuk.weather;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class WeatherInformation {

    public String dt;
    public Main main;
    public Weather[] weather;
    public Clouds clouds;
    public Wind wind;
    public Rain rain;
    public Sys sys;
    public String dt_txt;

    public WeatherInformation() {
        this.dt = "dt";
        this.main = null;
        this.weather = null;
        this.clouds = null;
        this.wind = null;
        this.rain = null;
        this.sys = null;
        this.dt_txt = "dt_txt";
    }

    public class Main {
        public float temp;
        public float temp_min;
        public float temp_max;
        public float pressure;
        public float sea_level;
        public float grnd_level;
        public int humidity;
        public double temp_kf;
    }

    public class Weather {
        public int id;
        public String main;
        public String description;
        public String icon;
    }

    public class Clouds {
        public int all;
    }

    public class Wind {
        public float speed;
        public float deg;
    }

    public class Rain {
        public float h3;
    }

    public class Sys {
        public String pod;
    }

// -- Методы для вывода информации --

    public String printTemp() {
        String temp = "";
        if (Math.round(this.main.temp) > 0) {
            temp = " +" + Math.round(this.main.temp) + "C";
        } else {
            temp = " " + Math.round(this.main.temp) + "C";
        }
        return temp;
    }

    public String printPressure() {
        return "Атм. давление: " + Math.round(this.main.pressure) + " мм.рт.ст.";
    }

    public String printHumidity() {
        return "Влажность воздуха: " + this.main.humidity + " %";
    }

    //!!! Добавить описание погоды (дощ, тучи..)
    public String printDescription(boolean simpleFormat) {
        String des = this.weather[0].description;
        if (!simpleFormat) {
            des = des.substring(0, 1).toUpperCase() + des.substring(1);
        }
        return des;
    }

    public String printWindSpeed() {
        return "скорость ветра: " + Math.round(this.wind.speed) + " м/с";
    }

    public String printWindDirection() {
        int d = Math.round(this.wind.deg);
        String direct = "";
        if (((d >= 0) && (d < 23)) || ((d >= 338) && (d <= 360))) {
            direct = "северный";        // (0° и 360°)
        } else if ((d >= 23) && (d < 68)) {
            direct = "северо-восточный"; //	45°
        } else if ((d >= 68) && (d < 113)) {
            direct = "восточный"; // 90°)
        } else if ((d >= 113) && (d < 158)) {
            direct = "юго-восточный"; // 135°
        } else if ((d >= 158) && (d < 203)) {
            direct = "южный"; // 180°
        } else if ((d >= 203) && (d < 248)) {
            direct = "юго-западый"; //225°
        } else if ((d >= 248) && (d < 273)) {
            direct = "западый"; // 270°
        } else if ((d >= 273) && (d < 338)) {
            direct = "северо-западный"; // 315°
        }
        direct = "Ветер " + direct + " (" + d + "°)" + ",";
        return direct;
    }

    private Date thisDate() {
        Date myDate = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            myDate = format.parse(dt_txt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myDate;
    }

    public String printDate() {
        Locale locale = new Locale("ru", "RU");
        SimpleDateFormat f = new SimpleDateFormat("dd MMMM", locale);
        return f.format(thisDate());
    }

    public String printTime() {
        SimpleDateFormat f = new SimpleDateFormat("HH:mm");
        return f.format(thisDate());
    }

    public String printDay(boolean shortForm) {
        Locale locale = new Locale("ru", "RU");
        Calendar c = new GregorianCalendar();
        c.setTime(thisDate());
        String day;
        if (shortForm) {
            day = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, locale);
            day = day.substring(0, 1).toUpperCase() + day.substring(1) + ".";
        } else {
            day = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale);
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


