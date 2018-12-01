package com.android.blawniczak.astroweather;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.blawniczak.astroweather.fragments.MoonFragment;
import com.android.blawniczak.astroweather.fragments.SunFragment;
import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AstroWeatherActivity extends AppCompatActivity {

    public static String regexDoubleNumber = "-?\\d+(\\.\\d+)?";
    @BindView(R.id.longitude) EditText longitude;
    @BindView(R.id.latitude) EditText latitude;
    @BindView(R.id.refresh_frequency) EditText refresh_frequency;
    @BindView(R.id.timer) TextView textTimer;

    private AstroCalculator astroCalculator;
    private Timer refreshTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_astro_weather);
        ButterKnife.bind(this);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        this.latitude.setText(sharedPref.getString(getString(R.string.latitude), getString(R.string.initial_latitude)));
        this.longitude.setText(sharedPref.getString(getString(R.string.longitude), getString(R.string.initial_longitude)));
        this.refresh_frequency.setText(sharedPref.getString(getString(R.string.refresh_frequency), getString(R.string.initial_frequency)));
        AstroCalculator.Location location = new AstroCalculator.Location(Double.parseDouble(this.latitude.getText().toString()), Double.parseDouble(this.longitude.getText().toString()));
        AstroDateTime astroDateTime = this.getCurrentAstroDateTime();
        this.astroCalculator = new AstroCalculator(astroDateTime, location);
        if (findViewById(R.id.vpPager) != null) {
            ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
            FragmentPagerAdapter adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
            vpPager.setAdapter(adapterViewPager);
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.sun_fragment, new SunFragment())
                    .commit();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.moon_fragment, new MoonFragment())
                    .commit();
        }
        this.refreshValuesTimerTask();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                textTimer.setText(DateFormat.getDateTimeInstance().format(new Date()));
            }
        },0, 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.refreshTimer.cancel();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.latitude), this.latitude.getText().toString());
        editor.putString(getString(R.string.longitude), this.longitude.getText().toString());
        editor.putString(getString(R.string.refresh_frequency), this.refresh_frequency.getText().toString());
        editor.commit();
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;

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
                default:
                    return "Page " + position;
            }
        }

    }

    public AstroCalculator getAstroCalculator() {
        return astroCalculator;
    }

    public AstroDateTime getCurrentAstroDateTime() {
        Calendar calendar = Calendar.getInstance();
        return new AstroDateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), 1, false);
    }

    @OnClick(R.id.save_button)
    public void clickOnSaveButton(){
        Toast toast = null;
        if(this.latitude.getText().toString().matches("") || this.longitude.getText().toString().matches("") || this.refresh_frequency.getText().toString().matches("")) {
            toast = Toast.makeText(AstroWeatherActivity.this, R.string.toast_plain_error, Toast.LENGTH_SHORT);
        } else if(!this.latitude.getText().toString().matches(regexDoubleNumber) || Math.abs(Double.parseDouble(this.latitude.getText().toString())) > 90) {
            toast = Toast.makeText(AstroWeatherActivity.this, R.string.toast_wrong_latitude, Toast.LENGTH_SHORT);
        } else if(!this.longitude.getText().toString().matches(regexDoubleNumber) || Math.abs(Double.parseDouble(this.longitude.getText().toString())) > 180) {
            toast = Toast.makeText(AstroWeatherActivity.this, R.string.toast_wrong_longitude, Toast.LENGTH_SHORT);
        } else if (!this.refresh_frequency.getText().toString().matches(regexDoubleNumber)) {
            toast = Toast.makeText(AstroWeatherActivity.this, R.string.toast_wrong_frequency, Toast.LENGTH_SHORT);
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

    private void refreshValues() {
        this.astroCalculator.setLocation(new AstroCalculator.Location(Double.parseDouble(this.latitude.getText().toString()), Double.parseDouble(this.longitude.getText().toString())));
        SunFragment sunFragment;
        MoonFragment moonFragment;
        if(findViewById(R.id.vpPager) != null) {
            sunFragment = (SunFragment) getSupportFragmentManager().findFragmentByTag(this.getFragmentTag(0));
            moonFragment = (MoonFragment) getSupportFragmentManager().findFragmentByTag(this.getFragmentTag(1));
        } else {
            sunFragment = (SunFragment) getSupportFragmentManager().findFragmentById(R.id.sun_fragment);
            moonFragment = (MoonFragment) getSupportFragmentManager().findFragmentById(R.id.moon_fragment);
        }
        sunFragment.calculateValues();
        moonFragment.calculateValues();
        Activity activity = AstroWeatherActivity.this;
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(AstroWeatherActivity.this, R.string.toast_values_refreshed, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
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
}
