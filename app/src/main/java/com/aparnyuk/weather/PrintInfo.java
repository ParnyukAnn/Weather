package com.aparnyuk.weather;

import com.aparnyuk.weather.ModelJR.WeatherInformation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class PrintInfo {
    private WeatherInformation w;

    public PrintInfo(WeatherInformation weather) {
        w = weather;
    }

    public String printTemp() {
        String temp = "";
        if (Math.round(w.getMain().getTemp()) > 0) {
            temp = " +" + Math.round(w.getMain().getTemp()) + "C";
        } else {
            temp = " " + Math.round(w.getMain().getTemp()) + "C";
        }
        return temp;
    }

    public String printPressure() {
        return "Атм. давление: " + Math.round(w.getMain().getPressure()) + " мм.рт.ст.";
    }

    public String printHumidity() {
        return "Влажность воздуха: " + w.getMain().getHumidity() + " %";
    }

    //!!! Дополнить описание погоды (дощ, тучи..)
    public String printDescription(boolean simpleFormat) {
        String des = w.getWeather().get(0).getDescription();
        if (!simpleFormat) {
            des = des.substring(0, 1).toUpperCase() + des.substring(1);
        }
        return des;
    }

    public String getIcon(){
        return w.getWeather().get(0).getIcon();
    }

    public String printWindSpeed() {
        return "скорость ветра: " + Math.round(w.getWind().getSpeed()) + " м/с";
    }

    public String printWindDirection() {
        int d = Math.round(w.getWind().getDeg());
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
            myDate = format.parse(w.getDt_txt());
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
