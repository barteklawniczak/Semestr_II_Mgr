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

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoonFragment extends AppCompatDialogFragment {

    @BindView(R.id.moon_rise_time) TextView moonRiseTime;
    @BindView(R.id.moon_set_time) TextView moonSetTime;
    @BindView(R.id.next_full_moon) TextView nextFullMoon;
    @BindView(R.id.next_new_moon) TextView nextNewMoon;
    @BindView(R.id.moon_illumination) TextView moonIllumination;
    @BindView(R.id.moon_age) TextView moonAge;
    public final static DecimalFormat resultFormat = new DecimalFormat("0.##");


    public static MoonFragment newInstance() {
        return new MoonFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_moon, parent, false);
        ButterKnife.bind(this, v);
        this.calculateValues();
        return v;
    }

    public void calculateValues() {
        AstroCalculator astroCalculator = ((AstroWeatherActivity)getActivity()).getAstroCalculator();
        this.moonRiseTime.setText(SunFragment.getTime(astroCalculator.getMoonInfo().getMoonrise()));
        this.moonSetTime.setText(SunFragment.getTime(astroCalculator.getMoonInfo().getMoonset()));
        this.nextFullMoon.setText(SunFragment.getDate(astroCalculator.getMoonInfo().getNextFullMoon()));
        this.nextNewMoon.setText(SunFragment.getDate(astroCalculator.getMoonInfo().getNextNewMoon()));
        this.moonIllumination.setText(String.format("%s%%", String.valueOf(resultFormat.format(astroCalculator.getMoonInfo().getIllumination() * 100))));
        this.moonAge.setText(String.format("%s days", String.valueOf(resultFormat.format(astroCalculator.getMoonInfo().getAge() / 10))));
    }
}
