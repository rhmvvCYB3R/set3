package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    private lateinit var display: TextView
    private lateinit var expressionDisplay: TextView

    private var currentInput = ""
    private var previousResult: Double? = null
    private var currentOperator: String? = null
    private var justEvaluated = false
    private var expressionText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.display)
        expressionDisplay = findViewById(R.id.expressionDisplay)
        display.text = "0"
        expressionDisplay.text = ""

        listOf(R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9)
            .forEach { id ->
                findViewById<Button>(id).setOnClickListener {
                    onDigitPressed((it as Button).text.toString())
                }
            }

        listOf(R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide, R.id.btnPower)
            .forEach { id ->
                findViewById<Button>(id).setOnClickListener {
                    onOperatorPressed((it as Button).text.toString())
                }
            }

        findViewById<Button>(R.id.btnEquals).setOnClickListener { onEqualsPressed() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { onClearPressed() }
    }

    private fun onDigitPressed(digit: String) {
        if (justEvaluated) {
            currentInput = ""
            previousResult = null
            currentOperator = null
            expressionText = ""
            justEvaluated = false
        }
        currentInput += digit
        expressionText = expressionText.trimEnd() + digit
        display.text = currentInput
        expressionDisplay.text = expressionText
    }

    private fun onOperatorPressed(op: String) {
        justEvaluated = false
        if (currentInput.isEmpty() && previousResult == null) return

        val opSymbol = when (op) {
            "num" -> " ^ "
            "+" -> " + "
            "-" -> " - "
            "*" -> " * "
            "/" -> " / "
            else -> " $op "
        }

        if (previousResult != null && currentInput.isNotEmpty()) {
            computePendingOperation()
            if (display.text == "Error") return
        } else if (previousResult == null && currentInput.isNotEmpty()) {
            previousResult = currentInput.toDoubleOrNull() ?: return
            currentInput = ""
        }

        currentOperator = op
        currentInput = ""
        expressionText += opSymbol
        expressionDisplay.text = expressionText
    }

    private fun onEqualsPressed() {
        if (currentOperator != null && currentInput.isNotEmpty()) {
            expressionDisplay.text = "$expressionText ="
            computePendingOperation()
            currentOperator = null
            justEvaluated = true
        }
    }

    private fun onClearPressed() {
        currentInput = ""
        previousResult = null
        currentOperator = null
        justEvaluated = false
        expressionText = ""
        display.text = "0"
        expressionDisplay.text = ""
    }

    private fun computePendingOperation() {
        val inputNumber = currentInput.toDoubleOrNull() ?: return
        val prev = previousResult ?: return

        try {
            val result = when (currentOperator) {
                "+" -> prev + inputNumber
                "-" -> prev - inputNumber
                "*" -> prev * inputNumber
                "/" -> {
                    if (inputNumber == 0.0) { showError(); return }
                    prev / inputNumber
                }
                "num" -> prev.pow(inputNumber)
                else -> prev
            }
            previousResult = result
            display.text = if (result == kotlin.math.floor(result) && !result.isInfinite())
                result.toLong().toString()
            else
                result.toString()
            currentInput = ""
        } catch (e: Exception) {
            showError()
        }
    }

    private fun showError() {
        display.text = "Error"
        expressionDisplay.text = ""
        previousResult = null
        currentInput = ""
        currentOperator = null
        expressionText = ""
        justEvaluated = false
    }
}