package com.android.blawniczak.astroweather.fragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.blawniczak.astroweather.AstroWeatherActivity;
import com.android.blawniczak.astroweather.R;
import com.android.blawniczak.astroweather.domain.weather.Channel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BasicWeatherFragment extends AppCompatDialogFragment {

    @BindView(R.id.temperature) TextView temperature;
    @BindView(R.id.date) TextView date;
    @BindView(R.id.pressure) TextView pressure;
    @BindView(R.id.condition) TextView condition;
    @BindView(R.id.condition_image) ImageView conditionImage;

    public static BasicWeatherFragment newInstance() {
        return new BasicWeatherFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_basic_weather, parent, false);
        ButterKnife.bind(this, v);
        this.calculateValues();
        return v;
    }

    public void calculateValues() {
        Channel weatherInfo = ((AstroWeatherActivity)getActivity()).getWeatherInfo();
        if(weatherInfo != null && this.date !=null) {
            this.date.setText(weatherInfo.item.condition.date);
            this.temperature.setText(weatherInfo.item.condition.temp + " °" + weatherInfo.units.temperature);
            this.pressure.setText(weatherInfo.atmosphere.pressure + " " + weatherInfo.units.pressure);
            this.condition.setText(weatherInfo.item.condition.text);
            int weatherCode = Integer.parseInt(weatherInfo.item.condition.code);
            if (weatherCode > 25 && weatherCode < 29) {
                this.conditionImage.setImageResource(R.drawable.mostly_cloudy);
            } else if ((weatherCode > 28 && weatherCode < 31) || (weatherCode > 32 && weatherCode < 35) || weatherCode == 44) {
                this.conditionImage.setImageResource(R.drawable.partly_cloudy);
            } else if (weatherCode == 32) {
                this.conditionImage.setImageResource(R.drawable.sunny);
            } else if (weatherCode == 31) {
                this.conditionImage.setImageResource(R.drawable.clear);
            } else if (weatherCode > 22 && weatherCode < 25) {
                this.conditionImage.setImageResource(R.drawable.windy);
            } else if (weatherCode > 4 && weatherCode < 13) {
                this.conditionImage.setImageResource(R.drawable.raining);
            } else if ((weatherCode > 12 && weatherCode < 17) || (weatherCode > 40 && weatherCode < 44)) {
                this.conditionImage.setImageResource(R.drawable.snowing);
            } else if (weatherCode >= 0 && weatherCode < 3) {
                this.conditionImage.setImageResource(R.drawable.tornados);
            } else if ((weatherCode > 2 && weatherCode < 5) || (weatherCode > 36 && weatherCode < 40) || (weatherCode > 44 && weatherCode < 48)) {
                this.conditionImage.setImageResource(R.drawable.thunderstorms);
            }
        }
    }
}
