package com.android.blawniczak.astroweather.fragments

import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.android.blawniczak.astroweather.AstroWeatherActivity
import com.android.blawniczak.astroweather.R
import com.astrocalculator.AstroCalculator
import com.astrocalculator.AstroDateTime

import butterknife.BindView
import butterknife.ButterKnife

class SunFragment : AppCompatDialogFragment() {

    @BindView(R.id.sun_rise_time)
    @JvmField internal var sunRiseTime: TextView? = null
    @BindView(R.id.sun_set_time)
    @JvmField internal var sunSetTime: TextView? = null
    @BindView(R.id.sun_rise_azimuth)
    @JvmField internal var sunRiseAzimuth: TextView? = null
    @BindView(R.id.sun_set_azimuth)
    @JvmField internal var sunSetAzimuth: TextView? = null
    @BindView(R.id.sun_twilight_morning_azimuth)
    @JvmField internal var sunTwilightMorning: TextView? = null
    @BindView(R.id.sun_twilight_evening_azimuth)
    @JvmField internal var sunTwilightEvening: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_sun, parent, false)
        ButterKnife.bind(this, v)
        this.calculateValues()
        return v
    }

    fun calculateValues() {
        val astroCalculator = (activity as AstroWeatherActivity).astroCalculator
        if (this.sunRiseTime != null) {
            this.sunRiseTime!!.text = SunFragment.getTime(astroCalculator!!.sunInfo.sunrise)
            this.sunSetTime!!.text = SunFragment.getTime(astroCalculator.sunInfo.sunset)
            this.sunRiseAzimuth!!.setText(String.format("%s°", MoonFragment.resultFormat.format(astroCalculator.sunInfo.azimuthRise).toString()))
            this.sunSetAzimuth!!.setText(String.format("%s°", MoonFragment.resultFormat.format(astroCalculator.sunInfo.azimuthSet).toString()))
            this.sunTwilightMorning!!.text = SunFragment.getTime(astroCalculator.sunInfo.twilightMorning)
            this.sunTwilightEvening!!.text = SunFragment.getTime(astroCalculator.sunInfo.twilightEvening)
        }
    }

    companion object {

        fun newInstance(): SunFragment {
            return SunFragment()
        }

        fun getTime(astroDateTime: AstroDateTime): String {
            return astroDateTime.hour.toString() + ":" + astroDateTime.minute
        }

        fun getDate(astroDateTime: AstroDateTime): String {
            return astroDateTime.day.toString() + "-" + astroDateTime.month + "-" + astroDateTime.year
        }
    }
}
