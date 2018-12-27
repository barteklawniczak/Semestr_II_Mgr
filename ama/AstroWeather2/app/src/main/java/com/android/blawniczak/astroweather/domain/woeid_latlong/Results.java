package com.android.blawniczak.astroweather.domain.woeid_latlong;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Results {

    @SerializedName("place")
    @Expose
    public Place place;

}
