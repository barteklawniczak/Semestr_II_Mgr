package com.android.blawniczak.astroweather.domain.woeid_city;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Results {

    @SerializedName("place")
    @Expose
    public Place place;

}
