package com.android.blawniczak.astroweather.domain.woeid_latlong;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Centroid {

    @SerializedName("latitude")
    @Expose
    public String latitude;

    @SerializedName("longitude")
    @Expose
    public String longitude;

}
