package com.android.blawniczak.astroweather.domain.weather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Wind {

    @SerializedName("chill")
    @Expose
    var chill: String? = null
    @SerializedName("direction")
    @Expose
    var direction: String? = null
    @SerializedName("speed")
    @Expose
    var speed: String? = null

}
