package com.aparnyuk.weather.ModelJR;

import io.realm.RealmObject;

public class Sys extends RealmObject {
    private String pod;

    public String getPod() {
        return pod;
    }

    public void setPod(String pod) {
        this.pod = pod;
    }
}
