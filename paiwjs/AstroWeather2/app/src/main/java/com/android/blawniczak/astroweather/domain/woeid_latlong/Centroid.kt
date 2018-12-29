package com.android.blawniczak.astroweather.domain.woeid_latlong

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Centroid {

    @SerializedName("latitude")
    @Expose
    var latitude: String? = null

    @SerializedName("longitude")
    @Expose
    var longitude: String? = null

}
