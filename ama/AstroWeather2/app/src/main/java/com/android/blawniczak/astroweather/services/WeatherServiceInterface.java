package com.android.blawniczak.astroweather.services;

import com.android.blawniczak.astroweather.domain.weather.Weather;
import com.android.blawniczak.astroweather.domain.woeid_city.WoeidCity;
import com.android.blawniczak.astroweather.domain.woeid_latlong.WoeidLatLong;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherServiceInterface {

    @GET("v1/public/yql")
    Call<WoeidLatLong> getWoeidLatLong(@Query("q") String query, @Query("format") String format);

    @GET("v1/public/yql")
    Call<WoeidCity> getWoeidCity(@Query("q") String query, @Query("format") String format);

    @GET("v1/public/yql")
    Call<Weather> getWeather(@Query("q") String query, @Query("format") String format);

}