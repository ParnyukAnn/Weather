package com.aparnyuk.weather.ModelJR;

import io.realm.RealmObject;

public class Rain  extends RealmObject {
    private float h3;

    public float getH3() {
        return h3;
    }

    public void setH3(float h3) {
        this.h3 = h3;
    }
}