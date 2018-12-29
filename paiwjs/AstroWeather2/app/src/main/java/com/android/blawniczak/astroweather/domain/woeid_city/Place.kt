package com.android.blawniczak.astroweather.domain.woeid_city

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Place {

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("woeid")
    @Expose
    var woeid: String? = null

}
