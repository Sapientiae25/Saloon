package com.example.saloon

import android.os.Bundle
import android.view.Gravity
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import java.time.YearMonth
import java.util.*

class CalendarActivity : AppCompatActivity(), UpdateCalendar  {
    private lateinit var rvDates: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        val accountItem = intent.getParcelableExtra<AccountItem>("account_item")
        val rvCalendar = findViewById<RecyclerView>(R.id.rvCalendar)
        val tvYear = findViewById<TextView>(R.id.tvYear)
        val tvMonth = findViewById<TextView>(R.id.tvMonth)
        val calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH) + 1
        rvDates = findViewById(R.id.rvDates)
        val months = mutableListOf("January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December")
        val years = mutableListOf<Int>()
        for (i in 0 until 10){ years.add(year+i) }
        var pair = daysInAMonth(month,year,day)
        var dates = pair.first
        var index = pair.second
        var firstTime = ""
        var position = 1
        val calendarArray = JSONArray()
        for (i in 0 until 5){
            for (h in 0 until 24){
                for (m in 0 until 60 step 15){
                    val interval = JSONObject()
                    if (m == 0){ firstTime = getString(R.string.clock,h,m) }
                    interval.put("value", "0");interval.put("position",position)
                    interval.put("startTime",firstTime)
                    interval.put("time", getString(R.string.clock,h,m))
                    if (position == 4){position=1}else{position+=1}
                    calendarArray.put(interval) }}}
        rvDates.adapter = DatesAdapter(dates,accountItem!!,this,calendarArray)
        rvDates.hasFixedSize()
        rvDates.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        rvDates.adapter?.notifyItemRangeInserted(0,dates.size)
        rvDates.smoothScrollToPosition(index)
        tvYear.text = year.toString()
        tvMonth.text = months[month]
        tvYear.setOnClickListener {
            val popupMenu = PopupMenu(this,tvYear,Gravity.BOTTOM)
            popupMenu.inflate(R.menu.time_menu)
            for (i in 0 until years.size){
                val text = years[i].toString()
                popupMenu.menu.add(0,i,i,text)}
            popupMenu.setOnMenuItemClickListener { item ->
                year = years[item.itemId]
                tvYear.text = years[item.itemId].toString()
                pair = daysInAMonth(month,year,day)
                dates = pair.first
                index = pair.second
                rvCalendar.adapter?.notifyItemRangeInserted(0,dates.size)
                true }
            popupMenu.show() }

        tvMonth.setOnClickListener {
            val popupMenu = PopupMenu(this,tvMonth,Gravity.BOTTOM)
            popupMenu.inflate(R.menu.time_menu)
            for (i in 0 until months.size){
                val text = months[i]
                popupMenu.menu.add(0,i,i,text)}
            popupMenu.setOnMenuItemClickListener { item ->
                tvMonth.text = months[item.itemId]
                month = item.itemId
                pair = daysInAMonth(month,year,day)
                dates = pair.first
                index = pair.second
                rvCalendar.adapter?.notifyItemRangeInserted(0,dates.size)
                true }
            popupMenu.show() }

    }

    private fun daysInAMonth( m: Int, year: Int,chosenDay: Int): Pair<MutableList<MutableList<String>>, Int> {
       val  month = m + 1
        val daysObj = YearMonth.of(year,month)
        val days = daysObj.lengthOfMonth()
        val dates = mutableListOf<String>()
        val dateList = mutableListOf<MutableList<String>>()
        for (day in 1 until days){
            dates.add(getString(R.string.datetime,year,month,day))
            val copy = dates.toMutableList()
            if (day % 5 == 0){dateList.add(copy); dates.clear()
            } }
        val num = chosenDay/days
        val mod = chosenDay % days
        val index = if (mod == 0) num else num+1
        return Pair(dateList, index)
    }
    override fun update(startPosition: Int, endPosition: Int) {
        rvDates.adapter?.notifyItemRangeChanged(startPosition,endPosition)
    }
}