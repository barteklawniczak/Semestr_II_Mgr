package com.android.blawniczak.astroweather.domain.woeid_latlong;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Place {

    @SerializedName("centroid")
    @Expose
    public Centroid centroid;

    @SerializedName("woeid")
    @Expose
    public String woeid;

}
