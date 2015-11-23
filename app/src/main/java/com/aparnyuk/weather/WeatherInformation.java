package com.aparnyuk.weather;

public class WeatherInformation  {

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
        public double temp;
        public double temp_min;
        public double temp_max;
        public double pressure;
        public double sea_level;
        public double grnd_level;
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
        public double speed;
        public double deg;
    }

    public class Rain {
        public double h3;
    }

    public class Sys {
        public String pod;
    }

}

