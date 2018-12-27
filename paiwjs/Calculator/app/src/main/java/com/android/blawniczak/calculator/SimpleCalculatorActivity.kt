package com.android.blawniczak.calculator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import net.objecthunter.exp4j.ExpressionBuilder

import java.text.DecimalFormat

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

class SimpleCalculatorActivity : AppCompatActivity() {

    @BindView(R.id.zero_button) @JvmField var mZeroButton: Button? = null
    @BindView(R.id.one_button) @JvmField var mOneButton: Button? = null
    @BindView(R.id.two_button) @JvmField var mTwoButton: Button? = null
    @BindView(R.id.three_button) @JvmField var mThreeButton: Button? = null
    @BindView(R.id.four_button) @JvmField var mFourButton: Button? = null
    @BindView(R.id.five_button) @JvmField var mFiveButton: Button? = null
    @BindView(R.id.six_button) @JvmField var mSixButton: Button? = null
    @BindView(R.id.seven_button) @JvmField var mSevenButton: Button? = null
    @BindView(R.id.eight_button) @JvmField var mEightButton: Button? = null
    @BindView(R.id.nine_button) @JvmField var mNineButton: Button? = null
    @BindView(R.id.divide_button) @JvmField var mDivideButton: Button? = null
    @BindView(R.id.multiply_button) @JvmField var mMultiplyButton: Button? = null
    @BindView(R.id.add_button) @JvmField var mAddButton: Button? = null
    @BindView(R.id.substract_button) @JvmField var mSubstractButton: Button? = null
    @BindView(R.id.equals_button) @JvmField var mEqualsButton: Button? = null
    @BindView(R.id.comma_button) @JvmField var mCommaButton: Button? = null
    @BindView(R.id.backspace_button) @JvmField var mBackspaceButton: Button? = null
    @BindView(R.id.reset_button) @JvmField var mResetButton: Button? = null
    @BindView(R.id.change_sign_button) @JvmField var mChangeSignButton: Button? = null
    @BindView(R.id.my_textview) @JvmField var mMyTextView: TextView? = null
    var operation: String = ""
    var operationPosition: Int = 0
    var functionWithBrackets: Boolean = false

    companion object {
        private val resultFormat = DecimalFormat("0.#####")
        private val operatorRegex = "[*\\-+.^]"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_calculator)
        ButterKnife.bind(this)
        this.operation = ""
        this.functionWithBrackets = false
    }

    @OnClick(
        R.id.zero_button,
        R.id.one_button,
        R.id.two_button,
        R.id.three_button,
        R.id.four_button,
        R.id.five_button,
        R.id.six_button,
        R.id.seven_button,
        R.id.eight_button,
        R.id.nine_button
    )
    fun clickOnNumber(button: Button) {
        this.clickOnNumberAction(button.text)
    }

    @OnClick(R.id.add_button, R.id.substract_button, R.id.divide_button, R.id.multiply_button)
    fun clickOnOperator(button: Button) {
        this.clickOnOperatorAction(button.text)
    }

    @OnClick(R.id.equals_button)
    fun clickOnEquals() {
        this.computeResult()
    }

    @OnClick(R.id.comma_button)
    fun clickOnComma(button: Button) {
        this.commaSeparator(button.text)
    }

    @OnClick(R.id.backspace_button)
    fun clickOnBackspace() {
        val currentString = mMyTextView!!.text.toString()
        if (!currentString.isEmpty()) {
            if (currentString[currentString.length - 1] != ')') {
                mMyTextView!!.text = currentString.substring(0, currentString.length - 1)
            } else {
                val indexOfBrace = currentString.lastIndexOf('(')
                if (currentString[indexOfBrace - 1] == 't') {
                    mMyTextView!!.text = currentString.substring(0, indexOfBrace - 4)
                } else {
                    mMyTextView!!.text = currentString.substring(0, indexOfBrace - 3)
                }
            }
        }
    }

    @OnClick(R.id.reset_button)
    fun clickOnReset() {
        this.resetCalculator()
    }

    @OnClick(R.id.change_sign_button)
    fun changeSign() {
        if (this.canPerformOperation()) {
            var currentText = mMyTextView!!.text.toString()
            if (this.operation.isEmpty()) {
                if (currentText.startsWith("-")) {
                    currentText = currentText.substring(1, currentText.length)
                } else {
                    currentText = "-$currentText"
                }
            } else {
                if (currentText[this.operationPosition + 1] == '-') {
                    currentText = currentText.substring(0, this.operationPosition + 1) +
                            currentText.substring(this.operationPosition + 2, currentText.length)
                } else {
                    currentText = currentText.substring(0, this.operationPosition + 1) + "-" +
                            currentText.substring(this.operationPosition + 1, currentText.length)
                }
            }
            mMyTextView!!.text = currentText
        } else {
            this.displayToast()
        }
    }

    private fun clickOnNumberAction(value: CharSequence) {
        if (this.functionWithBrackets && !mMyTextView!!.text.toString().isEmpty()) {
            var currentText = mMyTextView!!.text.toString()
            currentText = currentText.substring(0, currentText.length - 1) + value.toString() +
                    currentText.substring(currentText.length - 1, currentText.length)
            mMyTextView!!.text = currentText
        } else {
            mMyTextView!!.append(value)
        }
    }

    private fun clickOnOperatorAction(value: CharSequence) {
        if (this.functionWithBrackets) {
            if (!mMyTextView!!.text.toString()[mMyTextView!!.text.length - 1].toString().matches(operatorRegex.toRegex())) {
                this.functionWithBrackets = false
                this.performOperation(value)
            } else {
                this.displayToast()
            }
        } else {
            this.performOperation(value)
        }
    }

    private fun performOperation(value: CharSequence) {
        if (this.canPerformOperation()) {
            this.operationPosition = mMyTextView!!.text.length
            this.operation = value.toString()
            mMyTextView!!.append(value)
        } else {
            this.displayToast()
        }
    }

    private fun commaSeparator(value: CharSequence) {
        val currentValue: String
        if (this.operation.isEmpty()) {
            currentValue = mMyTextView!!.text.toString()
        } else {
            currentValue = mMyTextView!!.text.toString()
                .substring(this.operationPosition + 1, mMyTextView!!.text.length)
        }
        if (!currentValue.contains(".")) {
            if (this.canPerformOperation()) {
                if (this.functionWithBrackets) {
                    var currentText = mMyTextView!!.text.toString()
                    currentText = currentText.substring(0, currentText.length - 1) + value.toString() +
                            currentText.substring(currentText.length - 1, currentText.length)
                    mMyTextView!!.text = currentText
                } else {
                    mMyTextView!!.append(value)
                }
            } else {
                this.displayToast()
            }
        } else {
            this.displayToast()
        }
    }

    private fun computeResult() {
        val expressionToEvaluate = mMyTextView!!.text.toString()
        if (this.canPerformOperation()) {
            val calc = ExpressionBuilder(expressionToEvaluate).build()
            try {
                mMyTextView!!.text = resultFormat.format(calc.evaluate())
            } catch(e: ArithmeticException) {
                this.displayToast()
            }
            this.operation = ""
        } else {
            this.displayToast()
        }
    }

    private fun canPerformOperation(): Boolean {
        val currentText = mMyTextView!!.text.toString()
        return if (!currentText.isEmpty()) {
            !Character.toString(currentText[currentText.length - 1]).matches(operatorRegex.toRegex())
        } else {
            false
        }
    }

    private fun displayToast() {
        Toast.makeText(this@SimpleCalculatorActivity, R.string.wrong_operation_toast, Toast.LENGTH_SHORT).show()
    }

    private fun resetCalculator() {
        this.operation = ""
        mMyTextView!!.text = ""
    }
}
