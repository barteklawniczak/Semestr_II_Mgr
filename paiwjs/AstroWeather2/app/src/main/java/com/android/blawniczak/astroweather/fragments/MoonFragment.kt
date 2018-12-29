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

import java.text.DecimalFormat

import butterknife.BindView
import butterknife.ButterKnife

class MoonFragment : AppCompatDialogFragment() {

    @BindView(R.id.moon_rise_time)
    @JvmField internal var moonRiseTime: TextView? = null
    @BindView(R.id.moon_set_time)
    @JvmField internal var moonSetTime: TextView? = null
    @BindView(R.id.next_full_moon)
    @JvmField internal var nextFullMoon: TextView? = null
    @BindView(R.id.next_new_moon)
    @JvmField internal var nextNewMoon: TextView? = null
    @BindView(R.id.moon_illumination)
    @JvmField internal var moonIllumination: TextView? = null
    @BindView(R.id.moon_age)
    @JvmField internal var moonAge: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_moon, parent, false)
        ButterKnife.bind(this, v)
        this.calculateValues()
        return v
    }

    fun calculateValues() {
        val astroCalculator = (activity as AstroWeatherActivity).astroCalculator
        if (this.moonRiseTime != null) {
            this.moonRiseTime!!.text = SunFragment.getTime(astroCalculator!!.moonInfo.moonrise)
            this.moonSetTime!!.text = SunFragment.getTime(astroCalculator.moonInfo.moonset)
            this.nextFullMoon!!.text = SunFragment.getDate(astroCalculator.moonInfo.nextFullMoon)
            this.nextNewMoon!!.text = SunFragment.getDate(astroCalculator.moonInfo.nextNewMoon)
            this.moonIllumination!!.setText(String.format("%s%%", resultFormat.format(astroCalculator.moonInfo.illumination * 100).toString()))
            this.moonAge!!.setText(String.format("%s days", resultFormat.format(astroCalculator.moonInfo.age / 10).toString()))
        }
    }

    companion object {
        val resultFormat = DecimalFormat("0.##")


        fun newInstance(): MoonFragment {
            return MoonFragment()
        }
    }
}
