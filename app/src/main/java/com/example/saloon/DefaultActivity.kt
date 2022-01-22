package com.example.saloon

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.NumberPicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class DefaultActivity : AppCompatActivity(),RestartCalendar,ChangeFragment {

    var communicator: CloseSheet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)
        val accountItem = intent.getParcelableExtra<AccountItem>("account_item")!!
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        val userActivity = UserActivity.newInstance(accountItem)
        val calendarActivity = CalendarActivity.newInstance(accountItem)
        val updateFragment = UpdateFragment.initAccount(accountItem)


        replaceFragment(userActivity)

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId){
                R.id.miCalendar -> replaceFragment(calendarActivity)
                R.id.miHome -> replaceFragment(userActivity)
                R.id.miSettings -> replaceFragment(updateFragment)
            }
            true }
    }

    private fun replaceFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.activityFragment, fragment)
        transaction.addToBackStack("")
        transaction.commit()
    }

    override fun restart() {
        communicator?.close()
//        makeCalendar(chosenDay)
    }

    fun showCustomDialog(textView: TextView) {
        val dialog = Dialog(this)
        var hour = 0
        var minute = 0
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
            minute = x.toInt()
            textView.text = getString(R.string.clock,hour,minute)}
        close.setOnClickListener { dialog.dismiss() }
        save.setOnClickListener { println("$hour:$minute") ;dialog.dismiss() }
        dialog.show()
    }
    override fun change(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.activityFragment, fragment)
        transaction.addToBackStack("")
        transaction.commit()
    }

//    override fun onBackPressed() {
//        println(supportFragmentManager.backStackEntryCount)
//        if (supportFragmentManager.backStackEntryCount != 0) {
//            supportFragmentManager.popBackStack()
//        } else {
//            super.onBackPressed()
//        }    }
}