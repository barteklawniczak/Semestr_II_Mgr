package com.android.blawniczak.astroweather.domain.woeid_city

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Results {

    @SerializedName("place")
    @Expose
    var place: Place? = null

}
