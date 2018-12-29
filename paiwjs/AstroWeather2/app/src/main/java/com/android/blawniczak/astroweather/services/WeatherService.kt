package com.android.blawniczak.astroweather.services

import android.annotation.SuppressLint
import android.os.AsyncTask

import com.android.blawniczak.astroweather.domain.weather.Channel
import com.android.blawniczak.astroweather.domain.weather.Weather
import com.android.blawniczak.astroweather.domain.woeid_city.WoeidCity
import com.android.blawniczak.astroweather.domain.woeid_latlong.WoeidLatLong
import com.google.gson.Gson
import com.google.gson.GsonBuilder

import java.io.IOException
import java.util.concurrent.ExecutionException

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherService {


    fun getYahooLatLongInfo(city: String): com.android.blawniczak.astroweather.domain.woeid_latlong.Place? {
        @SuppressLint("StaticFieldLeak") val task = object : AsyncTask<String, Void, com.android.blawniczak.astroweather.domain.woeid_latlong.Place>() {

            override fun doInBackground(vararg params: String): com.android.blawniczak.astroweather.domain.woeid_latlong.Place? {
                val query = "SELECT woeid, centroid FROM geo.places(1) WHERE text=\"" + params[0] + "\""

                val gson = GsonBuilder()
                        .setLenient()
                        .create()

                val retrofit = Retrofit.Builder()
                        .baseUrl("https://query.yahooapis.com")
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build()

                val wService = retrofit.create<WeatherServiceInterface>(WeatherServiceInterface::class.java!!)

                val weather = wService.getWoeidLatLong(query, "json")
                try {
                    val resp = weather.execute()
                    if (resp.isSuccessful && resp.body()!!.query!!.results != null) {
                        return resp.body()!!.query!!.results!!.place
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                return null
            }
        }
        try {
            return task.execute(city).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        return null
    }


    fun getYahooCityInfo(latitude: String, longitude: String): com.android.blawniczak.astroweather.domain.woeid_city.Place? {
        @SuppressLint("StaticFieldLeak") val task = object : AsyncTask<String, Void, com.android.blawniczak.astroweather.domain.woeid_city.Place>() {

            override fun doInBackground(vararg params: String): com.android.blawniczak.astroweather.domain.woeid_city.Place? {
                val query = "SELECT * FROM geo.places WHERE text=\"(" + params[0] + "," + params[1] + ")\""

                val gson = GsonBuilder()
                        .setLenient()
                        .create()

                val retrofit = Retrofit.Builder()
                        .baseUrl("https://query.yahooapis.com")
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build()

                val wService = retrofit.create<WeatherServiceInterface>(WeatherServiceInterface::class.java!!)

                val weather = wService.getWoeidCity(query, "json")
                try {
                    val resp = weather.execute()
                    if (resp.isSuccessful && resp.body()!!.query!!.results != null) {
                        return resp.body()!!.query!!.results!!.place
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                return null
            }
        }
        try {
            return task.execute(latitude, longitude).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        return null
    }


    fun getWeatherInfo(unit: String?, woeid: String?): Channel? {
        @SuppressLint("StaticFieldLeak") val task = object : AsyncTask<String, Void, Channel>() {

            override fun doInBackground(vararg params: String): Channel? {
                val query = "SELECT * FROM weather.forecast WHERE woeid=" + params[1] + " and u=\"" + params[0] + "\""
                val gson = GsonBuilder()
                        .setLenient()
                        .create()

                val retrofit = Retrofit.Builder()
                        .baseUrl("https://query.yahooapis.com")
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build()

                val wService = retrofit.create<WeatherServiceInterface>(WeatherServiceInterface::class.java!!)

                val weather = wService.getWeather(query, "json")
                try {
                    val resp = weather.execute()
                    if (resp.isSuccessful && resp.body()!!.query!!.results != null) {
                        return resp.body()!!.query!!.results!!.channel
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                return null
            }
        }
        try {
            return task.execute(unit, woeid).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        return null
    }
}
