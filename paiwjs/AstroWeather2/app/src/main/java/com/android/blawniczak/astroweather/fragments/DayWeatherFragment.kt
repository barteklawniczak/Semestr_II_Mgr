package com.android.blawniczak.astroweather.fragments

import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.android.blawniczak.astroweather.R
import com.android.blawniczak.astroweather.domain.weather.Forecast

import butterknife.BindView
import butterknife.ButterKnife

class DayWeatherFragment : AppCompatDialogFragment() {

    @BindView(R.id.date)
    @JvmField internal var date: TextView? = null
    @BindView(R.id.day)
    @JvmField internal var day: TextView? = null
    @BindView(R.id.high)
    @JvmField internal var high: TextView? = null
    @BindView(R.id.low)
    @JvmField internal var low: TextView? = null
    @BindView(R.id.text)
    @JvmField internal var text: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_day_weather, parent, false)
        ButterKnife.bind(this, v)
        return v
    }

    fun calculateValues(forecast: Forecast) {
        if (this.date != null) {
            this.date!!.text = forecast.date
            this.day!!.text = forecast.day
            this.high!!.text = forecast.high
            this.low!!.text = forecast.low
            this.text!!.text = forecast.text
        }
    }

    companion object {

        fun newInstance(): DayWeatherFragment {
            return DayWeatherFragment()
        }
    }
}
