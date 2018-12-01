package com.android.blawniczak.astroweather.fragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.blawniczak.astroweather.AstroWeatherActivity;
import com.android.blawniczak.astroweather.R;
import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SunFragment extends AppCompatDialogFragment {

    @BindView(R.id.sun_rise_time) TextView sunRiseTime;
    @BindView(R.id.sun_set_time) TextView sunSetTime;
    @BindView(R.id.sun_rise_azimuth) TextView sunRiseAzimuth;
    @BindView(R.id.sun_set_azimuth) TextView sunSetAzimuth;
    @BindView(R.id.sun_twilight_morning_azimuth) TextView sunTwilightMorning;
    @BindView(R.id.sun_twilight_evening_azimuth) TextView sunTwilightEvening;

    public static SunFragment newInstance() {
        return new SunFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sun, parent, false);
        ButterKnife.bind(this, v);
        this.calculateValues();
        return v;
    }

    public void calculateValues() {
        AstroCalculator astroCalculator = ((AstroWeatherActivity)getActivity()).getAstroCalculator();
        this.sunRiseTime.setText(SunFragment.getTime(astroCalculator.getSunInfo().getSunrise()));
        this.sunSetTime.setText(SunFragment.getTime(astroCalculator.getSunInfo().getSunset()));
        this.sunRiseAzimuth.setText(String.format("%s°", String.valueOf(MoonFragment.resultFormat.format(astroCalculator.getSunInfo().getAzimuthRise()))));
        this.sunSetAzimuth.setText(String.format("%s°", String.valueOf(MoonFragment.resultFormat.format(astroCalculator.getSunInfo().getAzimuthSet()))));
        this.sunTwilightMorning.setText(SunFragment.getTime(astroCalculator.getSunInfo().getTwilightMorning()));
        this.sunTwilightEvening.setText(SunFragment.getTime(astroCalculator.getSunInfo().getTwilightEvening()));
    }

    public static String getTime(AstroDateTime astroDateTime) {
        return astroDateTime.getHour() + ":" + astroDateTime.getMinute();
    }

    public static String getDate(AstroDateTime astroDateTime) {
        return astroDateTime.getDay() + "-" + astroDateTime.getMonth() + "-" + astroDateTime.getYear();
    }
}
