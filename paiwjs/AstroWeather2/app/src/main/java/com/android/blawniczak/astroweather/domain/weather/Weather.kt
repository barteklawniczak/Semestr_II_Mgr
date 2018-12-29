package com.android.blawniczak.astroweather.domain.weather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Weather {

    @SerializedName("query")
    @Expose
    var query: Query? = null

}
