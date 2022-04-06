package com.example.saloon

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import java.time.YearMonth
import java.util.*


class Test : AppCompatActivity() {

    private var year: Int= 0
    private var month: Int = 0
    private var chosenDay: Int = 0
    lateinit var accountItem: AccountItem
    private val calendarList = mutableListOf<Triple<Int,Int,Int>>()
    private lateinit var tvYear: AutoCompleteTextView
    private lateinit var tvMonth: AutoCompleteTextView
    lateinit var next: ImageView
    private lateinit var previous: ImageView
    private lateinit var tvDate: AutoCompleteTextView
    var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_layout)
        val months = arrayOf("January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December")
        accountItem = intent.getParcelableExtra("account_item")!!
        val years = mutableListOf<Int>()
        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        chosenDay = calendar.get(Calendar.DAY_OF_MONTH)+1
        tvYear = findViewById(R.id.tvYear)
        tvMonth = findViewById(R.id.tvMonth)
        val vpDay = findViewById<ViewPager2>(R.id.vpDay)
        next = findViewById(R.id.next)
        previous = findViewById(R.id.previous)
        tvDate = findViewById(R.id.tvDate)
        vpDay.adapter = CalendarAdapter(calendarList,accountItem)

        for (i in 0 until 5){ years.add(year+i) }
        for (y in years){ for (m in 1 until 13){ calendarList.addAll(makeCalendar(m,y)) } }

        index = calendarList.indexOf(Triple(year,month+1,chosenDay))
        tvDate.setText(chosenDay.toString(),false)
        vpDay.setCurrentItem(index,false)

        var userDate = getString(R.string.user_date,chosenDay,month+1,year)
        tvYear.setText(userDate,false)
        tvMonth.setText(months[month],false)

        val monthArrayAdapter = ArrayAdapter(this,R.layout.text_layout,months)
        val yearArrayAdapter = ArrayAdapter(this,R.layout.text_layout,years.toList())

        tvYear.setAdapter(yearArrayAdapter)
        tvMonth.setAdapter(monthArrayAdapter)
        var dayList = getDays(month,year)
        tvDate.setAdapter(ArrayAdapter(this,R.layout.text_layout,dayList.toList()))
        tvDate.setOnClickListener{Log.println(Log.ASSERT,"tvDate",tvDate.adapter.count.toString())}

        vpDay.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val currentItem = calendarList[position]
                year = currentItem.first
                month = currentItem.second
                chosenDay = currentItem.third

                tvDate.setText(chosenDay.toString(),false)
                userDate = getString(R.string.user_date,chosenDay,month,year)
                tvYear.setText(userDate,false)
                tvMonth.setText(months[month-1],false)
                dayList = getDays(month,year)
                tvDate.setAdapter(ArrayAdapter(parent,R.layout.text_layout,dayList.toList()))

            } })
        tvDate.setOnItemClickListener { _, _, i, _ ->
            chosenDay = i+1
            userDate = getString(R.string.user_date,chosenDay,month,year)
            tvYear.setText(userDate,false)
            index = calendarList.indexOf(Triple(year,month,chosenDay))
            vpDay.setCurrentItem(index,false)}
        tvMonth.setOnItemClickListener { _, _, i, _ ->
            month = i+1
            dayList = getDays(month,year)
            tvDate.setAdapter(ArrayAdapter(this,R.layout.text_layout,dayList.toList()))
            val days = dayList.size
            chosenDay = if (chosenDay < days-1) chosenDay else days-1
            userDate = getString(R.string.user_date,chosenDay,month,year)
            tvYear.setText(userDate,false)
            index = calendarList.indexOf(Triple(year,month,chosenDay))
            vpDay.setCurrentItem(index,false)}
        tvYear.setOnItemClickListener { _, _, i, _ ->
            year = years[i+1]
            dayList = getDays(month,year)
            tvDate.setAdapter(ArrayAdapter(this,R.layout.text_layout,dayList.toList()))
            val days = dayList.size
            chosenDay = if (chosenDay < days-1) chosenDay else days-1
            userDate = getString(R.string.user_date,chosenDay,month,year)
            tvYear.setText(userDate,false)
            index = calendarList.indexOf(Triple(year,month,chosenDay))
            vpDay.setCurrentItem(index,false)}
        next.setOnClickListener {
            if (vpDay.currentItem != calendarList.size-1) {
                index = vpDay.currentItem+1
                val currentItem = calendarList[index]
                year = currentItem.first
                month = currentItem.second
                chosenDay = currentItem.third
                tvDate.setText(chosenDay.toString(),false)
                userDate = getString(R.string.user_date,chosenDay,month,year)
                tvYear.setText(userDate,false)
                tvMonth.setText(months[month-1],false)
                vpDay.setCurrentItem(index,true)
                dayList = getDays(month,year)
                tvDate.setAdapter(ArrayAdapter(this,R.layout.text_layout,dayList.toList()))} }
        previous.setOnClickListener {
            if (vpDay.currentItem != 0) {
                index = vpDay.currentItem-1
                val currentItem = calendarList[index]
                year = currentItem.first
                month = currentItem.second
                chosenDay = currentItem.third

                tvDate.setText(chosenDay.toString(),false)
                userDate = getString(R.string.user_date,chosenDay,month,year)
                tvYear.setText(userDate,false)
                tvMonth.setText(months[month-1],false)
                vpDay.setCurrentItem(index,true)
                dayList = getDays(month,year)
                tvDate.setAdapter(ArrayAdapter(this,R.layout.text_layout,dayList.toList()))}} }

    fun restart(){ Log.println(Log.ASSERT,"da",chosenDay.toString())
//        makeCalendar(chosenDay) TODO FIX
         }
    fun getDays(m: Int,y: Int): MutableList<String> {
        val days = mutableListOf<String>()
        for (i in 1 until YearMonth.of(y,m).lengthOfMonth()) {days.add(i.toString())}
        Log.println(Log.ASSERT,"days",days.toString())
        Log.println(Log.ASSERT,"count",days.size.toString())
        Log.println(Log.ASSERT,"m",m.toString())
        return days }
    private fun makeCalendar( month: Int, year: Int): MutableList<Triple<Int,Int,Int>> {
        val daysObj = YearMonth.of(year, month)
        val days = daysObj.lengthOfMonth()
        val dateList = mutableListOf<Triple<Int,Int,Int>>()
        for (day in 1 until days+1){ dateList.add(Triple(year, month, day)) }
        return dateList }
}