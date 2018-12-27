package com.android.blawniczak.astroweather.fragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.blawniczak.astroweather.AstroWeatherActivity;
import com.android.blawniczak.astroweather.R;
import com.android.blawniczak.astroweather.domain.weather.Channel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdditionalWeatherFragment extends AppCompatDialogFragment {

    @BindView(R.id.wind_direction) TextView windDirection;
    @BindView(R.id.wind_speed) TextView windSpeed;
    @BindView(R.id.visibility) TextView visibility;
    @BindView(R.id.humidity) TextView humidity;

    public static AdditionalWeatherFragment newInstance() { return new AdditionalWeatherFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_additional_weather, parent, false);
        ButterKnife.bind(this, v);
        this.calculateValues();
        return v;
    }

    public void calculateValues() {
        Channel weatherInfo = ((AstroWeatherActivity)getActivity()).getWeatherInfo();
        if(weatherInfo != null && this.windSpeed != null) {
            this.windDirection.setText(weatherInfo.wind.direction + "°");
            this.windSpeed.setText(weatherInfo.wind.speed + " " + weatherInfo.units.speed);
            this.humidity.setText(weatherInfo.atmosphere.humidity + "%");
            this.visibility.setText(weatherInfo.atmosphere.visibility + " " + weatherInfo.units.distance);
        }
    }

}
