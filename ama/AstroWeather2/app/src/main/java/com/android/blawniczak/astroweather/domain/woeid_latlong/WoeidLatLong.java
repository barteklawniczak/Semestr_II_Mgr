package com.android.blawniczak.astroweather.domain.woeid_latlong;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WoeidLatLong {

    @SerializedName("query")
    @Expose
    public Query query;

}
