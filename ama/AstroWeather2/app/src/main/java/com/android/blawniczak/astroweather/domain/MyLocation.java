package com.android.blawniczak.astroweather.domain;

import android.support.annotation.NonNull;

public class MyLocation {

    private String latitude;
    private String longitude;
    private String city;
    private String woeid;

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getCity() {
        return city;
    }

    public String getWoeid() {
        return woeid;
    }

    public MyLocation(String latitude, String longitude, String woeid, String city) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.woeid = woeid;
    }

    public MyLocation(String latitude, String longitude, String woeid) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = "";
        this.woeid = woeid;
    }

    @NonNull
    @Override
    public String toString() {
        return this.latitude + " " + this.longitude  + " " + this.woeid + " " + this.city;
    }
}
