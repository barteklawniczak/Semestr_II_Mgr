package com.android.blawniczak.astroweather.domain.woeid_latlong

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Place {

    @SerializedName("centroid")
    @Expose
    var centroid: Centroid? = null

    @SerializedName("woeid")
    @Expose
    var woeid: String? = null

}
