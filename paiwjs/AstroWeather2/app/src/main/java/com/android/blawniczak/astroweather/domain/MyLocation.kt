package com.android.blawniczak.astroweather.domain

class MyLocation {

    var latitude: String? = null
        private set
    var longitude: String? = null
        private set
    var city: String? = null
        private set
    var woeid: String? = null
        private set

    constructor(latitude: String?, longitude: String?, woeid: String?, city: String?) {
        this.latitude = latitude
        this.longitude = longitude
        this.city = city
        this.woeid = woeid
    }

    constructor(latitude: String?, longitude: String?, woeid: String?) {
        this.latitude = latitude
        this.longitude = longitude
        this.city = ""
        this.woeid = woeid
    }

    override fun toString(): String {
        return this.latitude + " " + this.longitude + " " + this.woeid + " " + this.city
    }
}
