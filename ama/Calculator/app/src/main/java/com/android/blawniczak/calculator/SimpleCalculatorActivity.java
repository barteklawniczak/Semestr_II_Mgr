package com.android.blawniczak.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SimpleCalculatorActivity extends AppCompatActivity {

    @BindView(R.id.zero_button) Button mZeroButton;
    @BindView(R.id.one_button) Button mOneButton;
    @BindView(R.id.two_button) Button mTwoButton;
    @BindView(R.id.three_button) Button mThreeButton;
    @BindView(R.id.four_button) Button mFourButton;
    @BindView(R.id.five_button) Button mFiveButton;
    @BindView(R.id.six_button) Button mSixButton;
    @BindView(R.id.seven_button) Button mSevenButton;
    @BindView(R.id.eight_button) Button mEightButton;
    @BindView(R.id.nine_button) Button mNineButton;
    @BindView(R.id.divide_button) Button mDivideButton;
    @BindView(R.id.multiply_button) Button mMultiplyButton;
    @BindView(R.id.add_button) Button mAddButton;
    @BindView(R.id.substract_button) Button mSubstractButton;
    @BindView(R.id.equals_button) Button mEqualsButton;
    @BindView(R.id.comma_button) Button mCommaButton;
    @BindView(R.id.backspace_button) Button mBackspaceButton;
    @BindView(R.id.reset_button) Button mResetButton;
    @BindView(R.id.change_sign_button) Button mChangeSignButton;
    @BindView(R.id.my_textview) TextView mMyTextView;

    protected final static DecimalFormat resultFormat = new DecimalFormat("0.#####");
    protected final static String operatorRegex = "[*\\-+.^]";
    protected String operation;
    protected int operationPosition;
    protected boolean functionWithBrackets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_calculator);
        ButterKnife.bind(this);
        this.operation = "";
        this.functionWithBrackets = false;
    }

    @OnClick({R.id.zero_button, R.id.one_button, R.id.two_button, R.id.three_button, R.id.four_button,
    R.id.five_button, R.id.six_button, R.id.seven_button, R.id.eight_button, R.id.nine_button})
    public void clickOnNumber(Button button) {
        this.clickOnNumberAction(button.getText());
    }

    @OnClick({R.id.add_button, R.id.substract_button, R.id.divide_button, R.id.multiply_button})
    public void clickOnOperator(Button button) {
        this.clickOnOperatorAction(button.getText());
    }

    @OnClick(R.id.equals_button)
    public void clickOnEquals(){
        this.computeResult();
    }

    @OnClick(R.id.comma_button)
    public void clickOnComma(Button button) {
        this.commaSeparator(button.getText());
    }

    @OnClick(R.id.backspace_button)
    public void clickOnBackspace() {
        String currentString = mMyTextView.getText().toString();
        if(!currentString.isEmpty()) {
            if(!(currentString.charAt(currentString.length()-1)==')')) {
                mMyTextView.setText(currentString.substring(0, currentString.length() - 1));
            } else {
                int indexOfBrace = currentString.lastIndexOf('(');
                if(currentString.charAt(indexOfBrace-1)=='t') {
                    mMyTextView.setText(currentString.substring(0, indexOfBrace-4));
                } else {
                    mMyTextView.setText(currentString.substring(0, indexOfBrace-3));
                }
            }
        }
    }

    @OnClick(R.id.reset_button)
    public void clickOnReset() { this.resetCalculator(); }

    @OnClick(R.id.change_sign_button)
    public void changeSign() {
        if(this.canPerformOperation()) {
            String currentText = mMyTextView.getText().toString();
            if (this.operation.isEmpty()) {
                if (currentText.startsWith("-")) {
                    currentText = currentText.substring(1, currentText.length());
                } else {
                    currentText = "-" + currentText;
                }
            } else {
                if (currentText.charAt(this.operationPosition + 1) == '-') {
                    currentText = currentText.substring(0, this.operationPosition+1) +
                            currentText.substring(this.operationPosition + 2, currentText.length());
                } else {
                    currentText = currentText.substring(0, this.operationPosition + 1) + "-" +
                            currentText.substring(this.operationPosition + 1, currentText.length());
                }
            }
            mMyTextView.setText(currentText);
        } else {
            this.displayToast();
        }
    }

    public void clickOnNumberAction(CharSequence value) {
        if(this.functionWithBrackets && !mMyTextView.getText().toString().isEmpty()) {
            String currentText = mMyTextView.getText().toString();
            currentText = currentText.substring(0, currentText.length()-1) + value.toString() +
                    currentText.substring(currentText.length()-1, currentText.length());
            mMyTextView.setText(currentText);
        } else {
            mMyTextView.append(value);
        }
    }

    public void clickOnOperatorAction(CharSequence value) {
        if(this.functionWithBrackets) {
            if(!String.valueOf(mMyTextView.getText().toString().charAt(mMyTextView.getText().length()-1)).matches(operatorRegex)) {
                this.functionWithBrackets = false;
                this.performOperation(value);
            } else {
                this.displayToast();
            }
        } else {
            this.performOperation(value);
        }
    }

    public void performOperation(CharSequence value) {
        if(this.canPerformOperation()){
            this.operationPosition = mMyTextView.getText().length();
            this.operation = value.toString();
            mMyTextView.append(value);
        } else {
            this.displayToast();
        }
    }

    void commaSeparator(CharSequence value) {
        String currentValue;
        if(this.operation.isEmpty()) {
            currentValue = mMyTextView.getText().toString();
        } else {
            currentValue = mMyTextView.getText().toString()
                    .substring(this.operationPosition+1, mMyTextView.getText().length());
        }
        if(!currentValue.contains(".")) {
            if (this.canPerformOperation()) {
                if(this.functionWithBrackets) {
                    String currentText = mMyTextView.getText().toString();
                    currentText = currentText.substring(0, currentText.length()-1) + value.toString() +
                            currentText.substring(currentText.length()-1, currentText.length());
                    mMyTextView.setText(currentText);
                } else {
                    mMyTextView.append(value);
                }
            } else {
                this.displayToast();
            }
        } else {
            this.displayToast();
        }
    }

    void computeResult() {
        String expressionToEvaluate = mMyTextView.getText().toString();
        if(this.canPerformOperation()) {
            Expression calc = new ExpressionBuilder(expressionToEvaluate).build();
            mMyTextView.setText(resultFormat.format(calc.evaluate()));
            this.operation = "";
        } else {
            this.displayToast();
        }
    }

    boolean canPerformOperation() {
        String currentText = mMyTextView.getText().toString();
        if(!currentText.isEmpty()) {
            return !(Character.toString(currentText.charAt(currentText.length() - 1)).matches(operatorRegex));
        } else {
            return false;
        }
    }

    void displayToast() {
        Toast.makeText(SimpleCalculatorActivity.this, R.string.wrong_operation_toast, Toast.LENGTH_SHORT).show();
    }

    void resetCalculator() {
        this.operation="";
        mMyTextView.setText("");
    }
}
