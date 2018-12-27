package com.android.blawniczak.astroweather.domain.woeid_city;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Place {

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("woeid")
    @Expose
    public String woeid;

}
