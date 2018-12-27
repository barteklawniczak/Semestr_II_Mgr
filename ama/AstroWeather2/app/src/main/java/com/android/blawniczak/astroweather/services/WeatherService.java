package com.android.blawniczak.astroweather.services;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.android.blawniczak.astroweather.domain.weather.Channel;
import com.android.blawniczak.astroweather.domain.weather.Weather;
import com.android.blawniczak.astroweather.domain.woeid_city.WoeidCity;
import com.android.blawniczak.astroweather.domain.woeid_latlong.WoeidLatLong;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherService {


    public com.android.blawniczak.astroweather.domain.woeid_latlong.Place getYahooLatLongInfo(String city) {
        @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, com.android.blawniczak.astroweather.domain.woeid_latlong.Place> task
                = new AsyncTask<String, Void, com.android.blawniczak.astroweather.domain.woeid_latlong.Place>() {

            @Override
            protected com.android.blawniczak.astroweather.domain.woeid_latlong.Place doInBackground(String... params) {
                String query = "SELECT woeid, centroid FROM geo.places(1) WHERE text=\"" + params[0] + "\"";

                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://query.yahooapis.com")
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                WeatherServiceInterface wService = retrofit.create(WeatherServiceInterface.class);

                Call<WoeidLatLong> weather = wService.getWoeidLatLong(query, "json");
                try {
                    Response<WoeidLatLong> resp = weather.execute();
                    if (resp.isSuccessful() && resp.body().query.results != null) {
                        return resp.body().query.results.place;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        try {
            return task.execute(city).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    public com.android.blawniczak.astroweather.domain.woeid_city.Place getYahooCityInfo(String latitude, String longitude) {
        @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, com.android.blawniczak.astroweather.domain.woeid_city.Place> task =
                new AsyncTask<String, Void, com.android.blawniczak.astroweather.domain.woeid_city.Place>() {

            @Override
            protected com.android.blawniczak.astroweather.domain.woeid_city.Place doInBackground(String... params) {
                String query = "SELECT * FROM geo.places WHERE text=\"(" + params[0] + "," + params[1] + ")\"";

                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://query.yahooapis.com")
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                WeatherServiceInterface wService = retrofit.create(WeatherServiceInterface.class);

                Call<WoeidCity> weather = wService.getWoeidCity(query, "json");
                try {
                    Response<WoeidCity> resp = weather.execute();
                    if (resp.isSuccessful() && resp.body().query.results != null) {
                        return resp.body().query.results.place;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        try {
            return task.execute(latitude, longitude).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Channel getWeatherInfo(String unit, String woeid) {
        @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, Channel> task = new AsyncTask<String, Void, Channel>() {

                    @Override
                    protected Channel doInBackground(String... params) {
                        String query = "SELECT * FROM weather.forecast WHERE woeid=" + params[1] + " and u=\"" + params[0] + "\"";
                        Gson gson = new GsonBuilder()
                                .setLenient()
                                .create();

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("https://query.yahooapis.com")
                                .addConverterFactory(GsonConverterFactory.create(gson))
                                .build();

                        WeatherServiceInterface wService = retrofit.create(WeatherServiceInterface.class);

                        Call<Weather> weather = wService.getWeather(query, "json");
                        try {
                            Response<Weather> resp = weather.execute();
                            if (resp.isSuccessful() && resp.body().query.results != null) {
                                return resp.body().query.results.channel;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
        try {
            return task.execute(unit, woeid).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
