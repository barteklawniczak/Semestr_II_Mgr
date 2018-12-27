package com.android.blawniczak.astroweather.fragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.blawniczak.astroweather.AstroWeatherActivity;
import com.android.blawniczak.astroweather.R;
import com.android.blawniczak.astroweather.domain.weather.Channel;
import com.android.blawniczak.astroweather.domain.weather.Forecast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class NextWeatherFragment extends AppCompatDialogFragment {

    private List<DayWeatherFragment> nextWeatherFragments;

    public static NextWeatherFragment newInstance() { return new NextWeatherFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nextWeatherFragments = new ArrayList<>();
        for(int i=0; i<9; i++) {
            nextWeatherFragments.add(DayWeatherFragment.newInstance());
        }
        getChildFragmentManager().beginTransaction().replace(R.id.next_weather_fragment_1, nextWeatherFragments.get(0)).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.next_weather_fragment_2, nextWeatherFragments.get(1)).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.next_weather_fragment_3, nextWeatherFragments.get(2)).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.next_weather_fragment_4, nextWeatherFragments.get(3)).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.next_weather_fragment_5, nextWeatherFragments.get(4)).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.next_weather_fragment_6, nextWeatherFragments.get(5)).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.next_weather_fragment_7, nextWeatherFragments.get(6)).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.next_weather_fragment_8, nextWeatherFragments.get(7)).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.next_weather_fragment_9, nextWeatherFragments.get(8)).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_next_weather, parent, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.calculateValues();
    }

    public void calculateValues() {
        Channel weatherInfo = ((AstroWeatherActivity)getActivity()).getWeatherInfo();
        if(weatherInfo != null) {
            List<Forecast> forecast = weatherInfo.item.forecast;
            for (int i = 0; i < this.nextWeatherFragments.size(); i++) {
                this.nextWeatherFragments.get(i).calculateValues(forecast.get(i));
            }
        }
    }

}
