package com.android.blawniczak.calculator

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

class MainActivity : AppCompatActivity() {

    @BindView(R.id.simple_button) @JvmField var simpleButton: Button? = null
    @BindView(R.id.exit_button) @JvmField var exitButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
    }

    @OnClick(R.id.simple_button)
    fun launchSimpleCalculator() {
        val i = Intent(this@MainActivity, SimpleCalculatorActivity::class.java)
        startActivity(i)
    }

    @OnClick(R.id.exit_button)
    fun exit() {
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(homeIntent)
    }
}
