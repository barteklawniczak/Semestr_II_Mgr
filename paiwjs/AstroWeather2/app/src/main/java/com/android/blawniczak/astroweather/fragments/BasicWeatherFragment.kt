package com.android.blawniczak.astroweather.fragments

import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.android.blawniczak.astroweather.AstroWeatherActivity
import com.android.blawniczak.astroweather.R
import com.android.blawniczak.astroweather.domain.weather.Channel

import butterknife.BindView
import butterknife.ButterKnife

class BasicWeatherFragment : AppCompatDialogFragment() {

    @BindView(R.id.temperature)
    @JvmField internal var temperature: TextView? = null
    @BindView(R.id.date)
    @JvmField internal var date: TextView? = null
    @BindView(R.id.pressure)
    @JvmField internal var pressure: TextView? = null
    @BindView(R.id.condition)
    @JvmField internal var condition: TextView? = null
    @BindView(R.id.condition_image)
    @JvmField internal var conditionImage: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_basic_weather, parent, false)
        ButterKnife.bind(this, v)
        this.calculateValues()
        return v
    }

    fun calculateValues() {
        val weatherInfo = (activity as AstroWeatherActivity).weatherInfo
        if (weatherInfo != null && this.date != null) {
            this.date!!.text = weatherInfo.item!!.condition!!.date
            this.temperature!!.text = weatherInfo.item!!.condition!!.temp + " °" + weatherInfo.units!!.temperature
            this.pressure!!.text = weatherInfo.atmosphere!!.pressure + " " + weatherInfo.units!!.pressure
            this.condition!!.text = weatherInfo.item!!.condition!!.text
            val weatherCode = Integer.parseInt(weatherInfo.item!!.condition!!.code!!)
            if (weatherCode > 25 && weatherCode < 29) {
                this.conditionImage!!.setImageResource(R.drawable.mostly_cloudy)
            } else if (weatherCode > 28 && weatherCode < 31 || weatherCode > 32 && weatherCode < 35 || weatherCode == 44) {
                this.conditionImage!!.setImageResource(R.drawable.partly_cloudy)
            } else if (weatherCode == 32) {
                this.conditionImage!!.setImageResource(R.drawable.sunny)
            } else if (weatherCode == 31) {
                this.conditionImage!!.setImageResource(R.drawable.clear)
            } else if (weatherCode > 22 && weatherCode < 25) {
                this.conditionImage!!.setImageResource(R.drawable.windy)
            } else if (weatherCode > 4 && weatherCode < 13) {
                this.conditionImage!!.setImageResource(R.drawable.raining)
            } else if (weatherCode > 12 && weatherCode < 17 || weatherCode > 40 && weatherCode < 44) {
                this.conditionImage!!.setImageResource(R.drawable.snowing)
            } else if (weatherCode >= 0 && weatherCode < 3) {
                this.conditionImage!!.setImageResource(R.drawable.tornados)
            } else if (weatherCode > 2 && weatherCode < 5 || weatherCode > 36 && weatherCode < 40 || weatherCode > 44 && weatherCode < 48) {
                this.conditionImage!!.setImageResource(R.drawable.thunderstorms)
            }
        }
    }

    companion object {

        fun newInstance(): BasicWeatherFragment {
            return BasicWeatherFragment()
        }
    }
}
