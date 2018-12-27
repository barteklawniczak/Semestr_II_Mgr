package com.android.blawniczak.astroweather.domain.woeid_city;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WoeidCity {

    @SerializedName("query")
    @Expose
    public Query query;

}
