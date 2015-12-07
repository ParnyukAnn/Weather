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
            temp = " +" + Math.round(w.getMain().getTemp()) + "C°";
        } else {
            temp = " " + Math.round(w.getMain().getTemp()) + "C°";
        }
        return temp;
    }

    public String printPressure() {
        return "Атм. давл.: " + Math.round(w.getMain().getPressure()*0.7500637) + " мм.рт.ст.";
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
        return "Cкорость ветра: " + Math.round(w.getWind().getSpeed()) + " м/с";
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
        direct = "Ветер " + direct + ": " + d + "°";
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
