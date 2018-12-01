package com.android.blawniczak.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.simple_button) Button simpleButton;
    @BindView(R.id.advanced_button) Button advancedButton;
    @BindView(R.id.about_button) Button aboutButton;
    @BindView(R.id.exit_button) Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.simple_button)
    public void launchSimpleCalculator() {
        Intent i = new Intent(MainActivity.this, SimpleCalculatorActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.advanced_button)
    public void launchAdvancedCalculator() {
        Intent i = new Intent(MainActivity.this, AdvancedCalculatorActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.about_button)
    public void showAbout() {
        Intent i = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.exit_button)
    public void exit() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}
