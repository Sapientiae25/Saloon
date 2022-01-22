package com.example.saloon

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class Tester : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tester)

        val tvHelp = findViewById<TextView>(R.id.tvHelp)
        val time = LocalTime.parse("13:15")
        val time2 = LocalTime.parse("11:36")

        if (time > time2){
            println("hoooo")
        }else{println("tooo")}
        println(time)
        tvHelp.setOnClickListener { showDialog(tvHelp) }
//        val numPickerHour = findViewById<NumberPicker>(R.id.numPickerHour)
//        val numPickerMins = findViewById<NumberPicker>(R.id.numPickerMins)
//        val save = findViewById<TextView>(R.id.save)
//        var hour = 0
//        var minute = ""
//
//        numPickerHour.minValue = 0
//        numPickerHour.maxValue  = 23
//
//        val minOptions = arrayOf("0","15","30","45")
//        numPickerMins.minValue = 0
//        numPickerMins.maxValue = 3
//        numPickerMins.displayedValues = minOptions
//
//        numPickerHour.setOnValueChangedListener { numberPicker, _, i2 ->  hour = numberPicker.value}
//        numPickerMins.setOnValueChangedListener { numberPicker, i, i2 ->
//            val x = minOptions[numberPicker.value]
//            minute = x}
//        save.setOnClickListener { println("$hour:$minute") }
    }

    private fun showDialog(textView: TextView) {
        val dialog = Dialog(this)
        var hour = 0
        var minute = ""
        val minOptions = arrayOf("0","15","30","45")
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.time_picker_layout)
        val numPickerHour = dialog.findViewById<NumberPicker>(R.id.numPickerHour)
        val numPickerMins = dialog.findViewById<NumberPicker>(R.id.numPickerMins)
        val save = dialog.findViewById<TextView>(R.id.save)
        val close = dialog.findViewById<TextView>(R.id.close)

        numPickerHour.minValue = 0
        numPickerHour.maxValue  = 23
        numPickerMins.minValue = 0
        numPickerMins.maxValue = 3
        numPickerMins.displayedValues = minOptions
        numPickerHour.setOnValueChangedListener { numberPicker, _, _ ->  hour = numberPicker.value}
        numPickerMins.setOnValueChangedListener { numberPicker, _, _ ->
            val x = minOptions[numberPicker.value]
            minute = x
            textView.text = minute}
        close.setOnClickListener { dialog.dismiss() }
        save.setOnClickListener { println("$hour:$minute") ;dialog.dismiss() }
        dialog.show()
    }
}