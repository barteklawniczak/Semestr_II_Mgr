package com.android.blawniczak.calculator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class AdvancedCalculatorActivity extends SimpleCalculatorActivity {

    @BindView(R.id.sinus_button) @Nullable Button mSinusButton;
    @BindView(R.id.cosinus_button) @Nullable Button mCosinusButton;
    @BindView(R.id.tangens_button) @Nullable Button mTangensButton;
    @BindView(R.id.natural_logarithm_button) @Nullable Button mNaturalLogarithmButton;
    @BindView(R.id.sqare_root_button) @Nullable Button mSquareRootButton;
    @BindView(R.id.second_power_button) @Nullable Button mSecondPowerButton;
    @BindView(R.id.power_button) @Nullable Button mPowerButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_calculator);
        ButterKnife.bind(this);
    }

    @Optional
    @OnClick(R.id.power_button)
    public void clickOnPowerButton() { this.clickOnOperatorAction("^"); }

    @Optional
    @OnClick(R.id.second_power_button)
    public void clickOnSecondPowerButton() {
        if(String.valueOf(mMyTextView.getText().charAt(mMyTextView.getText().length()-1)).matches("[0-9]")) {
            mMyTextView.append("^2");
        } else {
            this.displayToast();
        }
    }

    @Optional
    @OnClick({R.id.sinus_button,R.id.cosinus_button, R.id.tangens_button,
            R.id.natural_logarithm_button, R.id.sqare_root_button})
    public void clickOnActionButton(Button button) {
        if(this.canPerformFunction()) {
            this.selectFunction(button.getText().toString());
        } else {
            this.displayToast();
        }
    }

    public void selectFunction(String action) {
        String functionToAppend;
        switch(action) {
            case "sin":
                functionToAppend="sin()";
                break;
            case "cos":
                functionToAppend="cos()";
                break;
            case "tan":
                functionToAppend="tan()";
                break;
            case "ln":
                functionToAppend="log()";
                break;
            case "sqrt":
                functionToAppend="sqrt()";
                break;
            default:
                functionToAppend="";
                break;
        }
        this.functionWithBrackets = true;
        mMyTextView.append(functionToAppend);
    }

    public boolean canPerformFunction() {
        return mMyTextView.getText().toString().isEmpty() ||
                String.valueOf(mMyTextView.getText().charAt(mMyTextView.getText().length()-1)).matches(operatorRegex);
    }
}
