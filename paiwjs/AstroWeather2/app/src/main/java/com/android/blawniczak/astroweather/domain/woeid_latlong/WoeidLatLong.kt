package com.android.blawniczak.astroweather.domain.woeid_latlong

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WoeidLatLong {

    @SerializedName("query")
    @Expose
    var query: Query? = null

}
