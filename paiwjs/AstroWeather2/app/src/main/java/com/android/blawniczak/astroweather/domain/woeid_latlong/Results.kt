package com.android.blawniczak.astroweather.domain.woeid_latlong

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Results {

    @SerializedName("place")
    @Expose
    var place: Place? = null

}
