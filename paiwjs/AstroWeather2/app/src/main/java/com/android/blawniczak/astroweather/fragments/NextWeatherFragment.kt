package com.android.blawniczak.astroweather.fragments

import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.android.blawniczak.astroweather.AstroWeatherActivity
import com.android.blawniczak.astroweather.R
import com.android.blawniczak.astroweather.domain.weather.Channel
import com.android.blawniczak.astroweather.domain.weather.Forecast

import java.util.ArrayList

import butterknife.ButterKnife

class NextWeatherFragment : AppCompatDialogFragment() {

    private var nextWeatherFragments: MutableList<DayWeatherFragment>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nextWeatherFragments = ArrayList()
        for (i in 0..8) {
            nextWeatherFragments!!.add(DayWeatherFragment.newInstance())
        }
        childFragmentManager.beginTransaction().replace(R.id.next_weather_fragment_1, nextWeatherFragments!![0]).commit()
        childFragmentManager.beginTransaction().replace(R.id.next_weather_fragment_2, nextWeatherFragments!![1]).commit()
        childFragmentManager.beginTransaction().replace(R.id.next_weather_fragment_3, nextWeatherFragments!![2]).commit()
        childFragmentManager.beginTransaction().replace(R.id.next_weather_fragment_4, nextWeatherFragments!![3]).commit()
        childFragmentManager.beginTransaction().replace(R.id.next_weather_fragment_5, nextWeatherFragments!![4]).commit()
        childFragmentManager.beginTransaction().replace(R.id.next_weather_fragment_6, nextWeatherFragments!![5]).commit()
        childFragmentManager.beginTransaction().replace(R.id.next_weather_fragment_7, nextWeatherFragments!![6]).commit()
        childFragmentManager.beginTransaction().replace(R.id.next_weather_fragment_8, nextWeatherFragments!![7]).commit()
        childFragmentManager.beginTransaction().replace(R.id.next_weather_fragment_9, nextWeatherFragments!![8]).commit()
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_next_weather, parent, false)
        ButterKnife.bind(this, v)
        return v
    }

    override fun onStart() {
        super.onStart()
        this.calculateValues()
    }

    fun calculateValues() {
        val weatherInfo = (activity as AstroWeatherActivity).weatherInfo
        if (weatherInfo != null) {
            val forecast = weatherInfo.item!!.forecast
            for (i in this.nextWeatherFragments!!.indices) {
                this.nextWeatherFragments!![i].calculateValues(forecast!![i])
            }
        }
    }

    companion object {

        fun newInstance(): NextWeatherFragment {
            return NextWeatherFragment()
        }
    }

}
