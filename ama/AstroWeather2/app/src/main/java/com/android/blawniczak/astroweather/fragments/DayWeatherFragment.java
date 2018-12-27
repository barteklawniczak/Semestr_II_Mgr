package com.android.blawniczak.astroweather.fragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.blawniczak.astroweather.R;
import com.android.blawniczak.astroweather.domain.weather.Forecast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DayWeatherFragment extends AppCompatDialogFragment {

    @BindView(R.id.date) TextView date;
    @BindView(R.id.day) TextView day;
    @BindView(R.id.high) TextView high;
    @BindView(R.id.low) TextView low;
    @BindView(R.id.text) TextView text;

    public static DayWeatherFragment newInstance() {
        return new DayWeatherFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_day_weather, parent, false);
        ButterKnife.bind(this, v);
        return v;
    }

    public void calculateValues(Forecast forecast) {
        if(this.date != null) {
            this.date.setText(forecast.date);
            this.day.setText(forecast.day);
            this.high.setText(forecast.high);
            this.low.setText(forecast.low);
            this.text.setText(forecast.text);
        }
    }
}
