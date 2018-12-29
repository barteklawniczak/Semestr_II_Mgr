package com.android.blawniczak.astroweather.services

import com.android.blawniczak.astroweather.domain.weather.Weather
import com.android.blawniczak.astroweather.domain.woeid_city.WoeidCity
import com.android.blawniczak.astroweather.domain.woeid_latlong.WoeidLatLong

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherServiceInterface {

    @GET("v1/public/yql")
    fun getWoeidLatLong(@Query("q") query: String, @Query("format") format: String): Call<WoeidLatLong>

    @GET("v1/public/yql")
    fun getWoeidCity(@Query("q") query: String, @Query("format") format: String): Call<WoeidCity>

    @GET("v1/public/yql")
    fun getWeather(@Query("q") query: String, @Query("format") format: String): Call<Weather>

}