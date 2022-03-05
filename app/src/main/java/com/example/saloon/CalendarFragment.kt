package com.example.saloon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import java.time.YearMonth
import java.util.*


class CalendarFragment : Fragment() {

    lateinit var rvCalendar: RecyclerView
    private lateinit var dates: MutableList<String>
    val calendarList =  mutableListOf<CalendarItem>()
    lateinit var timeScrollListener: RecyclerView.OnScrollListener
    lateinit var calendarScrollListener: RecyclerView.OnScrollListener
    private var year: Int= 0
    private var month: Int = 0
    private var chosenDay: Int = 0
    private val timesBarList = mutableListOf<String>()
    lateinit var accountItem: AccountItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_calendar, container, false)
        (activity as DefaultActivity).supportActionBar?.title = "Calendar"
        accountItem = (activity as DefaultActivity).accountItem
        val months = mutableListOf("January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December")
        val years = mutableListOf<Int>()
        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        chosenDay = calendar.get(Calendar.DAY_OF_MONTH)
        rvCalendar = rootView.findViewById(R.id.rvCalendar)
        rvCalendar.layoutManager = LinearLayoutManager(context)
        rvCalendar.setHasFixedSize(true)
        rvCalendar.adapter = CalendarAdapter(calendarList,this)
        val tvYear = rootView.findViewById<AutoCompleteTextView>(R.id.tvYear)
        val tvMonth = rootView.findViewById<AutoCompleteTextView>(R.id.tvMonth)
        val rvTimesBar = rootView.findViewById<RecyclerView>(R.id.rvTimesBar)
        val next = rootView.findViewById<ImageView>(R.id.next)
        val previous = rootView.findViewById<ImageView>(R.id.previous)
        val tvDate = rootView.findViewById<TextView>(R.id.tvDate)
        for (i in 0 until 10){ years.add(year+i) }
        dates = daysInAMonth(month,year)
        makeCalendar(chosenDay)
        rvTimesBar.adapter = TimesBarAdapter(timesBarList)
        rvTimesBar.hasFixedSize()
        rvTimesBar.layoutManager = LinearLayoutManager(context)
        rvTimesBar.adapter?.notifyItemRangeInserted(0,timesBarList.size)
        var userDate = getString(R.string.user_date,chosenDay,month+1,year)
        tvYear.setText(userDate)
        tvMonth.setText(months[month])
        val monthArrayAdapter = ArrayAdapter(requireContext(),R.layout.text_layout,months.toTypedArray())
        val yearArrayAdapter = ArrayAdapter(requireContext(),R.layout.text_layout,years.toTypedArray())
        tvYear.setAdapter(yearArrayAdapter)
        tvMonth.setAdapter(monthArrayAdapter)
        tvMonth.setOnItemClickListener { _, _, i, _ ->
            month = i
            userDate = getString(R.string.user_date,chosenDay,month+1,year)
            tvYear.setText(userDate)
            dates = daysInAMonth(month,year)
            makeCalendar(chosenDay)}
        tvYear.setOnItemClickListener { _, _, i, _ ->
            year = years[i]
            userDate = getString(R.string.user_date,chosenDay,month+1,year)
            tvYear.setText(userDate)
            makeCalendar(chosenDay)
            dates = daysInAMonth(month,year) }
        next.setOnClickListener {
            if (chosenDay != dates.size){ chosenDay += 1
                tvDate.text = chosenDay.toString()
                userDate = getString(R.string.user_date,chosenDay,month+1,year)
                tvYear.setText(userDate)
                makeCalendar(chosenDay)
                rvCalendar.scrollToPosition(0)
                rvTimesBar.scrollToPosition(0)} }
        previous.setOnClickListener {
            if (chosenDay != 1){ chosenDay -= 1
                tvDate.text = chosenDay.toString()
                userDate = getString(R.string.user_date,chosenDay,month+1,year)
                tvYear.setText(userDate)
                makeCalendar(chosenDay)
                rvCalendar.scrollToPosition(0)
                rvTimesBar.scrollToPosition(0)} }
        tvDate.text = chosenDay.toString()

        timeScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                rvCalendar.removeOnScrollListener(calendarScrollListener)
                rvCalendar.scrollBy(dx, dy)
                rvCalendar.addOnScrollListener(calendarScrollListener) } }
        calendarScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                rvTimesBar.removeOnScrollListener(timeScrollListener)
                rvTimesBar.scrollBy(dx, dy)
                rvTimesBar.addOnScrollListener(timeScrollListener) }}
        rvTimesBar.addOnScrollListener(timeScrollListener)
        rvCalendar.addOnScrollListener(calendarScrollListener)

        return rootView
    }


    private fun makeCalendar(index: Int){
        val currentItem = dates[index-1]
        calendarList.clear()
        for (h in 0 until 24){
            calendarList.add(CalendarItem(getString(R.string.clock,h,0),
                getString(R.string.clock,h+1,0),date=getString(R.string.datetime,year,(month+1),chosenDay)))
            timesBarList.add(getString(R.string.clock,h+1,0)) }
        val url = getString(R.string.url,"calendar.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                Log.println(Log.ASSERT,"sui",response)
                val obj = JSONObject(response)
                val datesArray = obj.getJSONArray("dates")
                val breaksArray = obj.getJSONArray("break")
                for (x in 0 until datesArray.length()) {
                    val date = datesArray.getJSONObject(x)
                    val timePosition = date.getInt("position")
                    val rowCount = date.getInt("row_count")
                    val startBook = date.getString("start")
                    val endBook = date.getString("end")
                    val removeHours = date.getInt("remove_hours")
                    val firstSpan = date.getInt("first_span")
                    val finalSpan = date.getInt("final_span")
                    val bookingId = date.getInt("booking_id")
                    val styleId = date.getInt("style_id")
                    val calendarDate = date.getString("date")
                    if (firstSpan > 0){
                        val startHour = startBook.split(":")[0].toInt()
                        val previousItem = CalendarItem(getString(R.string.clock,startHour,0),
                            getString(R.string.clock,startHour+1,0),span=firstSpan,
                            date=getString(R.string.datetime,year,(month+1),chosenDay))
                        calendarList[timePosition-1] = previousItem }
                    for (y in 1 until removeHours+1){calendarList[timePosition+y].gone = true}
                    calendarList[timePosition+removeHours+1].span = finalSpan
                    val item = CalendarItem(startBook,endBook,span=rowCount,calendarType=2,date=calendarDate,id=bookingId,styleId=styleId)
                    calendarList[timePosition] = item
                }
                for (x in 0 until breaksArray.length()) {
                    val breaks = breaksArray.getJSONObject(x)
                    val timePosition = breaks.getInt("position")
                    val rowCount = breaks.getInt("row_count")
                    val startBreak = breaks.getString("start")
                    val endBreak = breaks.getString("end")
                    val calendarDate = breaks.getString("date")
                    val removeHours = breaks.getInt("remove_hours")
                    val firstSpan = breaks.getInt("first_span")
                    val finalSpan = breaks.getInt("final_span")
                    val breakId = breaks.getInt("id")
                    for (y in 1 until removeHours+1){ calendarList[timePosition+y].gone = true}
                    calendarList[timePosition+removeHours+1].span = finalSpan
                    val item = CalendarItem(startBreak,endBreak,"",rowCount,1,id=breakId,date=calendarDate)
                    calendarList[timePosition] = item
                    if (firstSpan > 0){
                        val startHour = startBreak.split(":")[0].toInt()
                        val previousItem = CalendarItem(getString(R.string.clock,startHour,0),
                            getString(R.string.clock,startHour+1,0),span=firstSpan,
                            date=getString(R.string.datetime,year,(month+1),chosenDay))
                        calendarList.add(timePosition,previousItem)
                    }}
                rvCalendar.adapter?.notifyItemRangeChanged(0,calendarList.size)
                rvCalendar.scrollToPosition(chosenDay) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["account_id"] = accountItem.id
                params["first_day"] = currentItem
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
    fun restart(){
        Log.println(Log.ASSERT,"da",chosenDay.toString())
        makeCalendar(chosenDay)}

    private fun daysInAMonth( m: Int, year: Int): MutableList<String> {
       val  month = m + 1
        val daysObj = YearMonth.of(year,month)
        val days = daysObj.lengthOfMonth()
        val dateList = mutableListOf<String>()
        for (day in 1 until days+1){ dateList.add(getString(R.string.datetime,year,month,day)) }
        return dateList
    }



}