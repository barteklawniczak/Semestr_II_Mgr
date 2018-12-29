package com.android.blawniczak.astroweather.domain.weather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Results {

    @SerializedName("channel")
    @Expose
    var channel: Channel? = null

}
