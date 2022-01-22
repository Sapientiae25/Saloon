package com.example.saloon

import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.NumberPicker
import android.widget.TimePicker
import java.util.*

class CustomTimePickerDialog(
    context: Context,
    listener: OnTimeSetListener,
    hourOfDay: Int,
    minute: Int,
    is24HourView: Boolean
) : TimePickerDialog(
    context,
    THEME_HOLO_LIGHT,
    null,
    hourOfDay,
    minute / 15,
    is24HourView
) {
    private val interval = 15
    private var mTimeSetListener: OnTimeSetListener = listener
    private lateinit var mTimePicker: TimePicker

    override fun updateTime(hourOfDay: Int, minuteOfHour: Int) {
        mTimePicker.hour = hourOfDay
        mTimePicker.minute = minuteOfHour / interval
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            BUTTON_POSITIVE ->
                mTimeSetListener.onTimeSet(
                    mTimePicker, mTimePicker.hour,
                    mTimePicker.minute * interval
                )
            BUTTON_NEGATIVE -> cancel()  } }
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        try {
            println("suii")
            val classForId = Class.forName("com.android.internal.R\$id")
//            val timePickerField = classForId.getField("timePicker")
//            mTimePicker = findViewById(timePickerField.getInt(null))
            val field = classForId.getField("minute")
            val minuteSpinner = mTimePicker.findViewById<NumberPicker>(field.getInt(null))

            minuteSpinner.minValue = 0
            minuteSpinner.maxValue = (60 / interval) - 1
            val displayedValues = ArrayList<String>()
            for (i in 0 until 60 step(interval)) {
                displayedValues.add(String.format("%02d", i))
            }
            minuteSpinner.displayedValues = displayedValues.toTypedArray()
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }
}