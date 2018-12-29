package com.android.blawniczak.astroweather.fragments

import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.android.blawniczak.astroweather.AstroWeatherActivity
import com.android.blawniczak.astroweather.R
import com.android.blawniczak.astroweather.domain.weather.Channel

import butterknife.BindView
import butterknife.ButterKnife

class AdditionalWeatherFragment : AppCompatDialogFragment() {

    @BindView(R.id.wind_direction)
    @JvmField internal var windDirection: TextView? = null
    @BindView(R.id.wind_speed)
    @JvmField internal var windSpeed: TextView? = null
    @BindView(R.id.visibility)
    @JvmField internal var visibility: TextView? = null
    @BindView(R.id.humidity)
    @JvmField internal var humidity: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_additional_weather, parent, false)
        ButterKnife.bind(this, v)
        this.calculateValues()
        return v
    }

    fun calculateValues() {
        val weatherInfo = (activity as AstroWeatherActivity).weatherInfo
        if (weatherInfo != null && this.windSpeed != null) {
            this.windDirection!!.text = weatherInfo.wind!!.direction!! + "°"
            this.windSpeed!!.text = weatherInfo.wind!!.speed + " " + weatherInfo.units!!.speed
            this.humidity!!.text = weatherInfo.atmosphere!!.humidity!! + "%"
            this.visibility!!.text = weatherInfo.atmosphere!!.visibility + " " + weatherInfo.units!!.distance
        }
    }

    companion object {

        fun newInstance(): AdditionalWeatherFragment {
            return AdditionalWeatherFragment()
        }
    }

}
