package com.android.blawniczak.astroweather

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import com.android.blawniczak.astroweather.domain.MyLocation
import com.android.blawniczak.astroweather.domain.weather.Channel
import com.android.blawniczak.astroweather.fragments.AdditionalWeatherFragment
import com.android.blawniczak.astroweather.fragments.BasicWeatherFragment
import com.android.blawniczak.astroweather.fragments.MoonFragment
import com.android.blawniczak.astroweather.fragments.NextWeatherFragment
import com.android.blawniczak.astroweather.fragments.SunFragment
import com.android.blawniczak.astroweather.services.WeatherService
import com.astrocalculator.AstroCalculator
import com.astrocalculator.AstroDateTime
import com.google.gson.Gson

import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.text.DateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Timer
import java.util.TimerTask

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnItemSelected

class AstroWeatherActivity : AppCompatActivity() {
    @BindView(R.id.longitude)
    @JvmField internal var longitude: EditText? = null
    @BindView(R.id.latitude)
    @JvmField internal var latitude: EditText? = null
    @BindView(R.id.refresh_frequency)
    @JvmField internal var refresh_frequency: EditText? = null
    @BindView(R.id.city)
    @JvmField internal var city: EditText? = null
    @BindView(R.id.timer)
    @JvmField internal var textTimer: TextView? = null
    @BindView(R.id.spinner)
    @JvmField internal var spinner: Spinner? = null
    @BindView(R.id.unitsSpinner)
    @JvmField internal var unitsSpinner: Spinner? = null

    var astroCalculator: AstroCalculator? = null
        private set
    private var refreshTimer: Timer? = null
    private var lastLatitude: String? = null
    private var lastLongitude: String? = null
    private var lastCity: String? = null
    private val weatherService = WeatherService()
    private var myLocations: MutableList<MyLocation>? = null
    private var arraySpinner: MutableList<String>? = null
    var weatherInfo: Channel? = null
        private set
    private var unit: String? = null

    val currentAstroDateTime: AstroDateTime
        get() {
            val calendar = Calendar.getInstance()
            return AstroDateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), 1, false)
        }

    val isOnline: Boolean
        get() {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_astro_weather)
        ButterKnife.bind(this)
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        // Arrays initialization
        this.arraySpinner = ArrayList()
        this.myLocations = ArrayList()
        this.readPlacesFromFile()
        val position = sharedPref.getInt(getString(R.string.position), -1)
        val unitPosition = sharedPref.getInt(getString(R.string.unit_position), -1)
        this.refresh_frequency!!.setText(sharedPref.getString(getString(R.string.refresh_frequency), getString(R.string.initial_frequency)))
        this.unit = sharedPref.getString(getString(R.string.units), "c")
        val weatherInfoString = sharedPref.getString(getString(R.string.weather), "")
        this.weatherInfo = Gson().fromJson<Channel>(weatherInfoString, Channel::class.java!!)
        if (this.arraySpinner!!.isEmpty()) {
            this.myLocations!!.add(MyLocation(getString(R.string.initial_latitude), getString(R.string.initial_longitude),
                    getString(R.string.initial_woeid), getString(R.string.initial_city)))
            this.arraySpinner!!.add("Lat: " + getString(R.string.initial_latitude) + " Lon: " + getString(R.string.initial_longitude) + " - " + getString(R.string.initial_city))
        }
        // Spinner initialization
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arraySpinner!!)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.spinner!!.adapter = adapter
        if (position != -1) {
            this.spinner!!.setSelection(position)
        } else {
            this.spinner!!.setSelection(0)
        }
        if (unitPosition != -1) {
            this.unitsSpinner!!.setSelection(unitPosition)
        } else {
            this.unitsSpinner!!.setSelection(0)
        }
        // AstroCalculator initialization
        val location = AstroCalculator.Location(
                java.lang.Double.parseDouble(sharedPref.getString(getString(R.string.latitude), getString(R.string.initial_latitude))!!),
                java.lang.Double.parseDouble(sharedPref.getString(getString(R.string.longitude), getString(R.string.initial_longitude))!!))
        val astroDateTime = this.currentAstroDateTime
        this.astroCalculator = AstroCalculator(astroDateTime, location)
        if (findViewById<View>(R.id.vpPager) != null) {
            val vpPager = findViewById<View>(R.id.vpPager) as ViewPager
            val adapterViewPager = MyPagerAdapter(supportFragmentManager)
            vpPager.adapter = adapterViewPager
        }
        // Check if device is online and set timer
        if (this.isOnline) {
            this.refreshValuesTimerTask()
        } else {
            this.noInternetConnectionToast()
        }
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                textTimer!!.text = DateFormat.getDateTimeInstance().format(Date())
            }
        }, 0, 1000)
    }

    public override fun onResume() {
        super.onResume()
    }

    @SuppressLint("ApplySharedPref")
    public override fun onPause() {
        super.onPause()
        if (isOnline) {
            this.refreshTimer!!.cancel()
        }
        this.writePlacesToFile()
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(getString(R.string.refresh_frequency), this.refresh_frequency!!.text.toString())
        editor.putString(getString(R.string.latitude), this.latitude!!.text.toString())
        editor.putString(getString(R.string.longitude), this.longitude!!.text.toString())
        editor.putString(getString(R.string.units), this.unit)
        editor.putInt(getString(R.string.position), this.spinner!!.selectedItemPosition)
        editor.putInt(getString(R.string.unit_position), this.unitsSpinner!!.selectedItemPosition)
        editor.putString(getString(R.string.weather), Gson().toJson(this.weatherInfo))
        editor.commit()
    }

    class MyPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

        override fun getCount(): Int {
            return NUM_ITEMS
        }

        override fun getItem(position: Int): Fragment? {
            when (position) {
                0 -> return SunFragment.newInstance()
                1 -> return MoonFragment.newInstance()
                2 -> return BasicWeatherFragment.newInstance()
                3 -> return AdditionalWeatherFragment.newInstance()
                4 -> return NextWeatherFragment.newInstance()
                else -> return null
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return "Sun Info"
                1 -> return "Moon Info"
                2 -> return "Basic Weather"
                3 -> return "Additional Weather"
                4 -> return "Next Weather"
                else -> return "Page $position"
            }
        }

        companion object {
            private val NUM_ITEMS = 5
        }

    }

    @OnItemSelected(R.id.spinner)
    fun spinnerItemSelected(position: Int) {
        val myLocation = this.myLocations!![position]
        this.latitude!!.setText(myLocation.latitude)
        this.longitude!!.setText(myLocation.longitude)
        if (myLocation.city != "") {
            this.city!!.setText(myLocation.city)
        }
        this.clickOnSaveButton()
    }

    @OnItemSelected(R.id.unitsSpinner)
    fun unitSpinnerItemSelected(position: Int) {
        when (position) {
            0 -> this.unit = "c"
            1 -> this.unit = "f"
            else -> {
            }
        }
        if (isOnline) {
            val woeid = this.myLocations!![this.spinner!!.selectedItemPosition].woeid
            this.downloadWeather(woeid)
            this.refreshValues()
        }
    }

    @SuppressLint("ShowToast")
    @OnClick(R.id.save_button)
    fun clickOnSaveButton() {
        var toast: Toast? = null
        if (!this.isOnline) {
            toast = Toast.makeText(this@AstroWeatherActivity, R.string.toast_no_internet_connection, Toast.LENGTH_SHORT)
        } else if (this.refresh_frequency!!.text.toString().matches("".toRegex())) {
            toast = Toast.makeText(this@AstroWeatherActivity, R.string.toast_plain_error_frequency, Toast.LENGTH_SHORT)
        } else if (!this.latitude!!.text.toString().matches(regexDoubleNumber.toRegex()) || Math.abs(java.lang.Double.parseDouble(this.latitude!!.text.toString())) > 90) {
            toast = Toast.makeText(this@AstroWeatherActivity, R.string.toast_wrong_latitude, Toast.LENGTH_SHORT)
        } else if (!this.longitude!!.text.toString().matches(regexDoubleNumber.toRegex()) || Math.abs(java.lang.Double.parseDouble(this.longitude!!.text.toString())) > 180) {
            toast = Toast.makeText(this@AstroWeatherActivity, R.string.toast_wrong_longitude, Toast.LENGTH_SHORT)
        } else if (!this.refresh_frequency!!.text.toString().matches(regexDoubleNumber.toRegex())) {
            toast = Toast.makeText(this@AstroWeatherActivity, R.string.toast_wrong_frequency, Toast.LENGTH_SHORT)
        } else if ((this.latitude!!.text.toString().matches("".toRegex()) || this.longitude!!.text.toString().matches("".toRegex())) && this.city!!.text.toString().matches("".toRegex())) {
            toast = Toast.makeText(this@AstroWeatherActivity, R.string.toast_plain_error, Toast.LENGTH_SHORT)
        }
        if (toast != null) {
            toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
            toast.show()
        } else {
            if (!this.city!!.text.toString().matches("".toRegex()) && this.city!!.text.toString() != this.lastCity) {
                val place = this.weatherService.getYahooLatLongInfo(this.city!!.text.toString())
                if (place == null) {
                    toast = Toast.makeText(this@AstroWeatherActivity, R.string.toast_wrong_city, Toast.LENGTH_SHORT)
                } else {
                    this.latitude!!.setText(place.centroid!!.latitude)
                    this.longitude!!.setText(place.centroid!!.longitude)
                    this.addLocationToList(place.centroid!!.latitude, place.centroid!!.longitude, place.woeid, this.city!!.text.toString())
                    this.downloadWeather(place.woeid)
                }
            } else if (this.latitude!!.text.toString() != this.lastLatitude || this.longitude!!.text.toString() != this.lastLongitude) {
                val place = this.weatherService.getYahooCityInfo(this.latitude!!.text.toString(), this.longitude!!.text.toString())
                if (place == null) {
                    toast = Toast.makeText(this@AstroWeatherActivity, R.string.toast_wrong_lat_or_lon, Toast.LENGTH_SHORT)
                } else {
                    if (!place.name!!.matches(regexPostalCode.toRegex())) {
                        this.city!!.setText(place.name)
                        this.addLocationToList(this.latitude!!.text.toString(), this.longitude!!.text.toString(), place.woeid, place.name)
                    } else {
                        this.addLocationToList(this.latitude!!.text.toString(), this.longitude!!.text.toString(), place.woeid, "")
                    }
                    this.downloadWeather(place.woeid)
                }
            }
            if (toast != null) {
                toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
                toast.show()
            } else {
                this.refreshValues()
                this.refreshTimer!!.cancel()
                this.refreshValuesTimerTask()
            }
        }
    }

    @OnClick(R.id.remove_button)
    fun removeLocation() {
        val position = this.spinner!!.selectedItemPosition
        if (this.myLocations!!.size < 2) {
            val toast = Toast.makeText(this@AstroWeatherActivity, R.string.toast_locations_not_empty, Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
            toast.show()
        } else {
            this.myLocations!!.removeAt(position)
            this.arraySpinner!!.removeAt(position)
            this.spinner!!.setSelection(0)
        }
    }

    fun downloadWeather(woeid: String?) {
        this.weatherInfo = this.weatherService.getWeatherInfo(this.unit, woeid)
    }

    fun addLocationToList(latitude: String?, longitude: String?, woeid: String?, city: String?) {
        val myLocation: MyLocation
        if (city != "") {
            myLocation = MyLocation(latitude, longitude, woeid, city)
        } else {
            myLocation = MyLocation(latitude, longitude, woeid)
        }
        val locationInString = "Lat: $latitude Lon: $longitude - $city"
        if (!this.arraySpinner!!.contains(locationInString)) {
            this.myLocations!!.add(myLocation)
            this.arraySpinner!!.add(locationInString)
            this.spinner!!.setSelection(this.arraySpinner!!.size - 1)
        }
    }

    private fun refreshValues() {
        this.lastLatitude = this.latitude!!.text.toString()
        this.lastLongitude = this.longitude!!.text.toString()
        this.lastCity = this.city!!.text.toString()
        this.astroCalculator!!.location = AstroCalculator.Location(java.lang.Double.parseDouble(this.latitude!!.text.toString()), java.lang.Double.parseDouble(this.longitude!!.text.toString()))
        var sunFragment: SunFragment? = null
        var moonFragment: MoonFragment? = null
        var basicWeatherFragment: BasicWeatherFragment? = null
        var additionalWeatherFragment: AdditionalWeatherFragment? = null
        var nextWeatherFragment: NextWeatherFragment? = null
        if (findViewById<View>(R.id.vpPager) != null) {
            sunFragment = supportFragmentManager.findFragmentByTag(this.getFragmentTag(0)) as SunFragment?
            moonFragment = supportFragmentManager.findFragmentByTag(this.getFragmentTag(1)) as MoonFragment?
            basicWeatherFragment = supportFragmentManager.findFragmentByTag(this.getFragmentTag(2)) as BasicWeatherFragment?
            additionalWeatherFragment = supportFragmentManager.findFragmentByTag(this.getFragmentTag(3)) as AdditionalWeatherFragment?
            nextWeatherFragment = supportFragmentManager.findFragmentByTag(this.getFragmentTag(4)) as NextWeatherFragment?
        }
        if (sunFragment != null && moonFragment != null && basicWeatherFragment != null && additionalWeatherFragment != null && nextWeatherFragment != null) {
            sunFragment.calculateValues()
            moonFragment.calculateValues()
            basicWeatherFragment.calculateValues()
            additionalWeatherFragment.calculateValues()
            nextWeatherFragment.calculateValues()
        }
        val activity = this@AstroWeatherActivity
        activity.runOnUiThread { showToast(getString(R.string.toast_values_refreshed)) }
    }

    private fun getFragmentTag(fragmentPosition: Int): String {
        return "android:switcher:" + R.id.vpPager + ":" + fragmentPosition
    }

    private fun refreshValuesTimerTask() {
        this.refreshTimer = Timer()
        this.refreshTimer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                refreshValues()
            }
        }, (java.lang.Double.parseDouble(this.refresh_frequency!!.text.toString()) * 60.0 * 1000.0).toInt().toLong(), (java.lang.Double.parseDouble(this.refresh_frequency!!.text.toString()) * 60.0 * 1000.0).toInt().toLong())
    }

    fun noInternetConnectionToast() {
        this.showToast(getString(R.string.toast_no_internet_connection))
    }

    fun writePlacesToFile() {
        val filename = "places.txt"
        val outputStream: FileOutputStream
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE)
            for (myLocation in myLocations!!) {
                outputStream.write((myLocation.toString() + "\n").toByteArray())
            }
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun readPlacesFromFile() {
        val context = this
        var fis: FileInputStream? = null
        try {
            fis = context.openFileInput("places.txt")
            val isr = InputStreamReader(fis)
            val bufferedReader = BufferedReader(isr)
            val sb = StringBuilder()
            var line: String? = null;
            while ({ line = bufferedReader.readLine(); line }() != null) {
                this.parseDataToPlace(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun parseDataToPlace(placeInString: String?) {
        val parts = placeInString!!.split("\\s+".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        if (parts.size == 4) {
            this.myLocations!!.add(MyLocation(parts[0], parts[1], parts[2], parts[3]))
            this.arraySpinner!!.add("Lat: " + parts[0] + " Lon: " + parts[1] + " - " + parts[3])
        } else if (parts.size == 3) {
            this.myLocations!!.add(MyLocation(parts[0], parts[1], parts[2]))
            this.arraySpinner!!.add("Lat: " + parts[0] + " Lon: " + parts[1])
        } else if (parts.size > 4) {
            val currentCity = StringBuilder()
            for (i in 3 until parts.size) {
                currentCity.append(" ").append(parts[i])
            }
            this.myLocations!!.add(MyLocation(parts[0], parts[1], parts[2], currentCity.toString()))
            this.arraySpinner!!.add("Lat: " + parts[0] + " Lon: " + parts[1] + " - " + currentCity.toString())
        }
    }

    fun showToast(toastText: String) {
        val toast = Toast.makeText(this@AstroWeatherActivity, toastText, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
        toast.show()
    }

    companion object {

        var regexDoubleNumber = "-?\\d+(\\.\\d+)?"
        var regexPostalCode = "-?\\d+(\\-\\d+)?"
    }

}
