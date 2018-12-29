package com.android.blawniczak.astroweather.domain.woeid_city

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WoeidCity {

    @SerializedName("query")
    @Expose
    var query: Query? = null

}
