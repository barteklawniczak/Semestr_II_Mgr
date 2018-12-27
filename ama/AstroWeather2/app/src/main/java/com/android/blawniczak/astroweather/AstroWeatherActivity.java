package com.android.blawniczak.astroweather;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.blawniczak.astroweather.domain.MyLocation;
import com.android.blawniczak.astroweather.domain.weather.Channel;
import com.android.blawniczak.astroweather.fragments.AdditionalWeatherFragment;
import com.android.blawniczak.astroweather.fragments.BasicWeatherFragment;
import com.android.blawniczak.astroweather.fragments.MoonFragment;
import com.android.blawniczak.astroweather.fragments.NextWeatherFragment;
import com.android.blawniczak.astroweather.fragments.SunFragment;
import com.android.blawniczak.astroweather.services.WeatherService;
import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class AstroWeatherActivity extends AppCompatActivity {

    public static String regexDoubleNumber = "-?\\d+(\\.\\d+)?";
    public static String regexPostalCode = "-?\\d+(\\-\\d+)?";
    @BindView(R.id.longitude) EditText longitude;
    @BindView(R.id.latitude) EditText latitude;
    @BindView(R.id.refresh_frequency) EditText refresh_frequency;
    @BindView(R.id.city) EditText city;
    @BindView(R.id.timer) TextView textTimer;
    @BindView(R.id.spinner) Spinner spinner;
    @BindView(R.id.unitsSpinner) Spinner unitsSpinner;

    private AstroCalculator astroCalculator;
    private Timer refreshTimer;
    private String lastLatitude;
    private String lastLongitude;
    private String lastCity;
    private WeatherService weatherService = new WeatherService();
    private List<MyLocation> myLocations;
    private List<String> arraySpinner;
    private Channel weatherInfo;
    private String unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_astro_weather);
        ButterKnife.bind(this);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        // Arrays initialization
        this.arraySpinner = new ArrayList<>();
        this.myLocations = new ArrayList<>();
        this.readPlacesFromFile();
        int position = sharedPref.getInt(getString(R.string.position),-1);
        int unitPosition = sharedPref.getInt(getString(R.string.unit_position),-1);
        this.refresh_frequency.setText(sharedPref.getString(getString(R.string.refresh_frequency), getString(R.string.initial_frequency)));
        this.unit = sharedPref.getString(getString(R.string.units), "c");
        String weatherInfoString = sharedPref.getString(getString(R.string.weather), "");
        this.weatherInfo = new Gson().fromJson(weatherInfoString, Channel.class);
        if(this.arraySpinner.isEmpty()) {
            this.myLocations.add(new MyLocation(getString(R.string.initial_latitude), getString(R.string.initial_longitude),
                    getString(R.string.initial_woeid), getString(R.string.initial_city)));
            this.arraySpinner.add("Lat: " +  getString(R.string.initial_latitude) + " Lon: " + getString(R.string.initial_longitude) + " - " +  getString(R.string.initial_city));
        }
        // Spinner initialization
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner.setAdapter(adapter);
        if(position != -1) { this.spinner.setSelection(position); } else { this.spinner.setSelection(0); }
        if(unitPosition != -1) { this.unitsSpinner.setSelection(unitPosition); } else { this.unitsSpinner.setSelection(0); }
        // AstroCalculator initialization
        AstroCalculator.Location location = new AstroCalculator.Location(
                Double.parseDouble(sharedPref.getString(getString(R.string.latitude), getString(R.string.initial_latitude))),
                Double.parseDouble(sharedPref.getString(getString(R.string.longitude), getString(R.string.initial_longitude))));
        AstroDateTime astroDateTime = this.getCurrentAstroDateTime();
        this.astroCalculator = new AstroCalculator(astroDateTime, location);
        if (findViewById(R.id.vpPager) != null) {
            ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
            FragmentPagerAdapter adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
            vpPager.setAdapter(adapterViewPager);
        }
        // Check if device is online and set timer
        if(this.isOnline()) {
            this.refreshValuesTimerTask();
        } else {
            this.noInternetConnectionToast();
        }
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                textTimer.setText(DateFormat.getDateTimeInstance().format(new Date()));
            }
        }, 0, 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void onPause() {
        super.onPause();
        if(isOnline()) {
            this.refreshTimer.cancel();
        }
        this.writePlacesToFile();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.refresh_frequency), this.refresh_frequency.getText().toString());
        editor.putString(getString(R.string.latitude), this.latitude.getText().toString());
        editor.putString(getString(R.string.longitude), this.longitude.getText().toString());
        editor.putString(getString(R.string.units), this.unit);
        editor.putInt(getString(R.string.position), this.spinner.getSelectedItemPosition());
        editor.putInt(getString(R.string.unit_position), this.unitsSpinner.getSelectedItemPosition());
        editor.putString(getString(R.string.weather), new Gson().toJson(this.weatherInfo));
        editor.commit();
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 5;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return SunFragment.newInstance();
                case 1:
                    return MoonFragment.newInstance();
                case 2:
                    return BasicWeatherFragment.newInstance();
                case 3:
                    return AdditionalWeatherFragment.newInstance();
                case 4:
                    return NextWeatherFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Sun Info";
                case 1:
                    return "Moon Info";
                case 2:
                    return "Basic Weather";
                case 3:
                    return "Additional Weather";
                case 4:
                    return "Next Weather";
                default:
                    return "Page " + position;
            }
        }

    }

    public AstroCalculator getAstroCalculator() {
        return astroCalculator;
    }

    public Channel getWeatherInfo() { return weatherInfo; }

    public AstroDateTime getCurrentAstroDateTime() {
        Calendar calendar = Calendar.getInstance();
        return new AstroDateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), 1, false);
    }

    @OnItemSelected(R.id.spinner)
    public void spinnerItemSelected(int position) {
        MyLocation myLocation = this.myLocations.get(position);
        this.latitude.setText(myLocation.getLatitude());
        this.longitude.setText(myLocation.getLongitude());
        if (!myLocation.getCity().equals("")) {
            this.city.setText(myLocation.getCity());
        }
        this.clickOnSaveButton();
    }

    @OnItemSelected(R.id.unitsSpinner)
    public void unitSpinnerItemSelected(int position) {
        switch(position) {
            case 0:
                this.unit = "c";
                break;
            case 1:
                this.unit = "f";
                break;
            default:
                break;
        }
        if(isOnline()) {
            String woeid = this.myLocations.get(this.spinner.getSelectedItemPosition()).getWoeid();
            this.downloadWeather(woeid);
            this.refreshValues();
        }
    }

    @SuppressLint("ShowToast")
    @OnClick(R.id.save_button)
    public void clickOnSaveButton(){
        Toast toast = null;
        if(!this.isOnline()) {
            toast = Toast.makeText(AstroWeatherActivity.this, R.string.toast_no_internet_connection, Toast.LENGTH_SHORT);
        } else if(this.refresh_frequency.getText().toString().matches("")) {
            toast = Toast.makeText(AstroWeatherActivity.this, R.string.toast_plain_error_frequency, Toast.LENGTH_SHORT);
        } else if(!this.latitude.getText().toString().matches(regexDoubleNumber) || Math.abs(Double.parseDouble(this.latitude.getText().toString())) > 90) {
            toast = Toast.makeText(AstroWeatherActivity.this, R.string.toast_wrong_latitude, Toast.LENGTH_SHORT);
        } else if(!this.longitude.getText().toString().matches(regexDoubleNumber) || Math.abs(Double.parseDouble(this.longitude.getText().toString())) > 180) {
            toast = Toast.makeText(AstroWeatherActivity.this, R.string.toast_wrong_longitude, Toast.LENGTH_SHORT);
        } else if (!this.refresh_frequency.getText().toString().matches(regexDoubleNumber)) {
            toast = Toast.makeText(AstroWeatherActivity.this, R.string.toast_wrong_frequency, Toast.LENGTH_SHORT);
        } else if ((this.latitude.getText().toString().matches("") || this.longitude.getText().toString().matches("")) && this.city.getText().toString().matches("")) {
            toast = Toast.makeText(AstroWeatherActivity.this, R.string.toast_plain_error, Toast.LENGTH_SHORT);
        }
        if(toast != null) {
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        } else {
            if(!this.city.getText().toString().matches("") && !this.city.getText().toString().equals(this.lastCity)) {
                com.android.blawniczak.astroweather.domain.woeid_latlong.Place place = this.weatherService.getYahooLatLongInfo(this.city.getText().toString());
                if(place == null) {
                    toast = Toast.makeText(AstroWeatherActivity.this, R.string.toast_wrong_city, Toast.LENGTH_SHORT);
                } else {
                    this.latitude.setText(place.centroid.latitude);
                    this.longitude.setText(place.centroid.longitude);
                    this.addLocationToList(place.centroid.latitude, place.centroid.longitude, place.woeid, this.city.getText().toString());
                    this.downloadWeather(place.woeid);
                }
            } else if(!(this.latitude.getText().toString().equals(this.lastLatitude)) || !(this.longitude.getText().toString().equals(this.lastLongitude))) {
                com.android.blawniczak.astroweather.domain.woeid_city.Place place =
                        this.weatherService.getYahooCityInfo(this.latitude.getText().toString(), this.longitude.getText().toString());
                if(place == null) {
                    toast = Toast.makeText(AstroWeatherActivity.this, R.string.toast_wrong_lat_or_lon, Toast.LENGTH_SHORT);
                } else {
                    if(!place.name.matches(regexPostalCode)) {
                        this.city.setText(place.name);
                        this.addLocationToList(this.latitude.getText().toString(), this.longitude.getText().toString(), place.woeid, place.name);
                    } else {
                        this.addLocationToList(this.latitude.getText().toString(), this.longitude.getText().toString(), place.woeid, "");
                    }
                    this.downloadWeather(place.woeid);
                }
            }
            if(toast != null) {
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            } else {
                    this.refreshValues();
                    this.refreshTimer.cancel();
                    this.refreshValuesTimerTask();
            }
        }
    }

    @OnClick(R.id.remove_button)
    public void removeLocation() {
        int position = this.spinner.getSelectedItemPosition();
        if(this.myLocations.size() < 2) {
            Toast toast = Toast.makeText(AstroWeatherActivity.this, R.string.toast_locations_not_empty, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        } else {
            this.myLocations.remove(position);
            this.arraySpinner.remove(position);
            this.spinner.setSelection(0);
        }
    }

    public void downloadWeather(String woeid) {
        this.weatherInfo = this.weatherService.getWeatherInfo(this.unit, woeid);
    }

    public void addLocationToList(String latitude, String longitude, String woeid, String city) {
        MyLocation myLocation;
        if(!city.equals("")) {
            myLocation = new MyLocation(latitude, longitude, woeid, city);
        } else {
            myLocation = new MyLocation(latitude, longitude, woeid);
        }
        String locationInString = "Lat: " + latitude + " Lon: " + longitude + " - " +  city;
        if(!this.arraySpinner.contains(locationInString)) {
            this.myLocations.add(myLocation);
            this.arraySpinner.add(locationInString);
            this.spinner.setSelection(this.arraySpinner.size()-1);
        }
    }

    private void refreshValues() {
        this.lastLatitude = this.latitude.getText().toString();
        this.lastLongitude = this.longitude.getText().toString();
        this.lastCity = this.city.getText().toString();
        this.astroCalculator.setLocation(new AstroCalculator.Location(Double.parseDouble(this.latitude.getText().toString()), Double.parseDouble(this.longitude.getText().toString())));
        SunFragment sunFragment = null;
        MoonFragment moonFragment = null;
        BasicWeatherFragment basicWeatherFragment = null;
        AdditionalWeatherFragment additionalWeatherFragment = null;
        NextWeatherFragment nextWeatherFragment = null;
        if(findViewById(R.id.vpPager) != null) {
            sunFragment = (SunFragment) getSupportFragmentManager().findFragmentByTag(this.getFragmentTag(0));
            moonFragment = (MoonFragment) getSupportFragmentManager().findFragmentByTag(this.getFragmentTag(1));
            basicWeatherFragment = (BasicWeatherFragment) getSupportFragmentManager().findFragmentByTag(this.getFragmentTag(2));
            additionalWeatherFragment = (AdditionalWeatherFragment) getSupportFragmentManager().findFragmentByTag(this.getFragmentTag(3));
            nextWeatherFragment = (NextWeatherFragment) getSupportFragmentManager().findFragmentByTag(this.getFragmentTag(4));
        }
        if(sunFragment!=null && moonFragment!=null && basicWeatherFragment!=null && additionalWeatherFragment !=null && nextWeatherFragment != null) {
            sunFragment.calculateValues();
            moonFragment.calculateValues();
            basicWeatherFragment.calculateValues();
            additionalWeatherFragment.calculateValues();
            nextWeatherFragment.calculateValues();
        }
        Activity activity = AstroWeatherActivity.this;
        activity.runOnUiThread(new Runnable() {
            public void run() {
                showToast(getString(R.string.toast_values_refreshed));
            }
        });
    }

    private String getFragmentTag(int fragmentPosition)
    {
        return "android:switcher:" + R.id.vpPager + ":" + fragmentPosition;
    }

    private void refreshValuesTimerTask() {
        this.refreshTimer = new Timer();
        this.refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refreshValues();
            }
        },(int) (Double.parseDouble(this.refresh_frequency.getText().toString())*60*1000), (int) (Double.parseDouble(this.refresh_frequency.getText().toString())*60*1000));
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void noInternetConnectionToast() {
        this.showToast(getString(R.string.toast_no_internet_connection));
    }

    public void writePlacesToFile() {
        String filename = "places.txt";
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            for(MyLocation myLocation : myLocations) {
                outputStream.write((myLocation.toString()+"\n").getBytes());
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readPlacesFromFile() {
        Context context = this;
        FileInputStream fis = null;
        try {
            fis = context.openFileInput("places.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                this.parseDataToPlace(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parseDataToPlace(String placeInString) {
        String[] parts = placeInString.split("\\s+");
        if(parts.length == 4) {
            this.myLocations.add(new MyLocation(parts[0], parts[1], parts[2], parts[3]));
            this.arraySpinner.add("Lat: " + parts[0] + " Lon: " + parts[1] + " - " +  parts[3]);
        } else if(parts.length == 3) {
            this.myLocations.add(new MyLocation(parts[0], parts[1], parts[2]));
            this.arraySpinner.add("Lat: " + parts[0] + " Lon: " + parts[1]);
        } else if(parts.length > 4) {
            StringBuilder currentCity= new StringBuilder();
            for(int i=3; i<parts.length; i++) {
                currentCity.append(" ").append(parts[i]);
            }
            this.myLocations.add(new MyLocation(parts[0], parts[1], parts[2], currentCity.toString()));
            this.arraySpinner.add("Lat: " + parts[0] + " Lon: " + parts[1] + " - " +  currentCity.toString());
        }
    }

    public void showToast(String toastText) {
        Toast toast = Toast.makeText(AstroWeatherActivity.this, toastText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

}
