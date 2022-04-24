package com.example.saloon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import java.time.YearMonth
import java.util.*


class CalendarFragment : Fragment() {

    private var year: Int= 0
    private var month: Int = 0
    private var chosenDay: Int = 0
    lateinit var accountItem: AccountItem
    private val calendarList = mutableListOf<Triple<Int,Int,Int>>()
    private lateinit var tvYear: AutoCompleteTextView
    private lateinit var tvMonth: AutoCompleteTextView
    lateinit var next: ImageView
    private lateinit var previous: ImageView
    private lateinit var tvDate: AppCompatSpinner
    private lateinit var vpDay: ViewPager2
    var userDate = ""
    var index = 0
    val appContext = this
    private val years = mutableListOf<Int>()
    lateinit var dayList: MutableList<String>
    val months = arrayOf("January", "February", "March", "April", "May", "June", "July", "August",
        "September", "October", "November", "December")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_calendar, container, false)
        (activity as DefaultActivity).supportActionBar?.title = "Calendar"
        accountItem = (activity as DefaultActivity).accountItem
        tvYear = rootView.findViewById(R.id.tvYear)
        tvMonth = rootView.findViewById(R.id.tvMonth)
        vpDay = rootView.findViewById(R.id.vpDay)
        next = rootView.findViewById(R.id.next)
        previous = rootView.findViewById(R.id.previous)
        tvDate = rootView.findViewById(R.id.tvDate)
        val swipeRefresh = rootView.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        loadData()

        swipeRefresh.setOnRefreshListener { loadData() ;swipeRefresh.isRefreshing = false}
        tvDate.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, i: Int, p3: Long) {
                if (i+1 != chosenDay){
                    chosenDay = i+1
                    userDate = getString(R.string.user_date,chosenDay,month,year)
                    tvYear.setText(userDate,false)
                    index = calendarList.indexOf(Triple(year,month,chosenDay))
                    vpDay.setCurrentItem(index,false) }}
            override fun onNothingSelected(p0: AdapterView<*>?) {} }
        vpDay.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val currentItem = calendarList[position]
                year = currentItem.first
                month = currentItem.second
                chosenDay = currentItem.third

                userDate = getString(R.string.user_date,chosenDay,month,year)
                tvYear.setText(userDate,false)
                tvMonth.setText(months[month-1],false)
                dayList = getDays(month,year)
                tvDate.adapter = ArrayAdapter(requireContext(),R.layout.text_layout,dayList.toList())
                tvDate.setSelection(chosenDay-1)
            } })
        tvMonth.setOnItemClickListener { _, _, i, _ ->
            month = i+1
            dayList = getDays(month,year)
            tvDate.adapter = ArrayAdapter(requireContext(),R.layout.text_layout,dayList.toList())
            val days = dayList.size
            Log.println(Log.ASSERT,"dayList","$chosenDay $days")

            chosenDay = if (chosenDay < days) chosenDay else days
            userDate = getString(R.string.user_date,chosenDay,month,year)
            tvYear.setText(userDate,false)
            index = calendarList.indexOf(Triple(year,month,chosenDay))
            tvDate.setSelection(chosenDay-1)
            vpDay.setCurrentItem(index,false)}
        tvYear.setOnItemClickListener { _, _, i, _ ->
            year = years[i]
            dayList = getDays(month,year)
            tvDate.adapter = ArrayAdapter(requireContext(),R.layout.text_layout,dayList.toList())
            val days = dayList.size
            chosenDay = if (chosenDay < days) chosenDay else days
            userDate = getString(R.string.user_date,chosenDay,month,year)
            tvYear.setText(userDate,false)
            index = calendarList.indexOf(Triple(year,month,chosenDay))
            tvDate.setSelection(chosenDay-1)
            vpDay.setCurrentItem(index,false)}
        next.setOnClickListener {
            if (vpDay.currentItem != calendarList.size-1) {
                index = vpDay.currentItem+1
                val currentItem = calendarList[index]
                year = currentItem.first
                month = currentItem.second
                chosenDay = currentItem.third
                userDate = getString(R.string.user_date,chosenDay,month,year)
                tvYear.setText(userDate,false)
                tvMonth.setText(months[month-1],false)
                vpDay.setCurrentItem(index,true)
                dayList = getDays(month,year)
                tvDate.adapter = ArrayAdapter(requireContext(),R.layout.text_layout,dayList.toList())
                tvDate.setSelection(chosenDay-1) } }
        previous.setOnClickListener {
            if (vpDay.currentItem != 0) {
                index = vpDay.currentItem-1
                val currentItem = calendarList[index]
                year = currentItem.first
                month = currentItem.second
                chosenDay = currentItem.third
                userDate = getString(R.string.user_date,chosenDay,month,year)
                tvYear.setText(userDate,false)
                tvMonth.setText(months[month-1],false)
                vpDay.setCurrentItem(index,true)
                dayList = getDays(month,year)
                tvDate.adapter = ArrayAdapter(requireContext(),R.layout.text_layout,dayList.toList())
                tvDate.setSelection(chosenDay-1) }}
        return rootView
    }
    private fun loadData(){
        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)+1
        chosenDay = calendar.get(Calendar.DAY_OF_MONTH)
        vpDay.adapter = CalendarAdapter(calendarList,accountItem,this)
        for (i in 0 until 5){ years.add(year+i) }
        for (y in years){ for (m in 1 until 13){ calendarList.addAll(makeCalendar(m,y)) } }
        vpDay.adapter?.notifyItemRangeInserted(0,calendarList.size)

        index = calendarList.indexOf(Triple(year,month,chosenDay))

        vpDay.setCurrentItem(index,false)

        userDate = getString(R.string.user_date,chosenDay,month,year)
        tvYear.setText(userDate,false)
        tvMonth.setText(months[month-1],false)

        val monthArrayAdapter = ArrayAdapter(requireContext(),R.layout.text_layout,months)
        val yearArrayAdapter = ArrayAdapter(requireContext(),R.layout.text_layout,years.toList())

        tvYear.setAdapter(yearArrayAdapter)
        tvMonth.setAdapter(monthArrayAdapter)
        dayList = getDays(month,year)
        tvDate.adapter = ArrayAdapter(requireContext(),R.layout.text_layout,dayList.toList())
        tvDate.setSelection(chosenDay-1)
    }

    fun restart(){ Log.println(Log.ASSERT,"da",chosenDay.toString())
//        makeCalendar(chosenDay) TODO FIX
    }
    fun getDays(m: Int,y: Int): MutableList<String> {
        val days = mutableListOf<String>()
        for (i in 1 until YearMonth.of(y,m).lengthOfMonth()+1) {days.add("$i")}
        return days }
    private fun makeCalendar( month: Int, year: Int): MutableList<Triple<Int,Int,Int>> {
        val daysObj = YearMonth.of(year, month)
        val days = daysObj.lengthOfMonth()
        val dateList = mutableListOf<Triple<Int,Int,Int>>()
        for (day in 1 until days+1){ dateList.add(Triple(year, month, day)) }
        return dateList }
}