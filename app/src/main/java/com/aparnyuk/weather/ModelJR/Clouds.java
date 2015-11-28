package com.aparnyuk.weather.ModelJR;

import io.realm.RealmObject;

public class Clouds  extends RealmObject {
    private int all;

    public int getAll() {
        return all;
    }

    public void setAll(int all) {
        this.all = all;
    }
}