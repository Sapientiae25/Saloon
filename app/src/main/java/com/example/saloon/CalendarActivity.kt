package com.example.saloon

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import java.time.YearMonth
import java.util.*


class CalendarActivity : Fragment() {
    lateinit var rvCalendar: RecyclerView
    private lateinit var dates: MutableList<String>
    lateinit var accountItem: AccountItem
    val calendarList =  mutableListOf<CalendarItem>()
    lateinit var timeScrollListener: RecyclerView.OnScrollListener
    lateinit var calendarScrollListener: RecyclerView.OnScrollListener
    private var year: Int= 0
    private var month: Int = 0
    private var chosenDay: Int = 0
    private val timesBarList = mutableListOf<String>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getParcelable<AccountItem>("accountItem")?.let {
            accountItem = it
        }
    }
    companion object {
        fun newInstance(param1: AccountItem) =
            CalendarActivity().apply {
                arguments = Bundle().apply {
                    putParcelable("accountItem", param1)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.activity_calendar, container, false)
        val months = mutableListOf("January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December")
        val years = mutableListOf<Int>()
        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        var chosenDay = calendar.get(Calendar.DAY_OF_MONTH)
        rvCalendar = rootView.findViewById(R.id.rvCalendar)
        rvCalendar.layoutManager = LinearLayoutManager(context)
        rvCalendar.setHasFixedSize(true)
        rvCalendar.adapter = CalendarAdapter(calendarList,accountItem,(activity as DefaultActivity))
        val tvYear = rootView.findViewById<TextView>(R.id.tvYear)
        val tvMonth = rootView.findViewById<TextView>(R.id.tvMonth)
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
        tvYear.text = year.toString()
        tvMonth.text = months[month]
        tvYear.setOnClickListener {
            val popupMenu = PopupMenu(context,tvYear,Gravity.BOTTOM)
            popupMenu.inflate(R.menu.time_menu)
            for (i in 0 until years.size){
                val text = years[i].toString()
                popupMenu.menu.add(0,i,i,text)}
            popupMenu.setOnMenuItemClickListener { item ->
                year = years[item.itemId]
                tvYear.text = years[item.itemId].toString()
                dates = daysInAMonth(month,year)
                rvCalendar.adapter?.notifyItemRangeInserted(0,dates.size)
                true }
            popupMenu.show() }

        tvMonth.setOnClickListener {
            val popupMenu = PopupMenu(context,tvMonth,Gravity.BOTTOM)
            popupMenu.inflate(R.menu.time_menu)
            for (i in 0 until months.size){
                val text = months[i]
                popupMenu.menu.add(0,i,i,text)}
            popupMenu.setOnMenuItemClickListener { item ->
                tvMonth.text = months[item.itemId]
                month = item.itemId
                dates = daysInAMonth(month,year)
                rvCalendar.adapter?.notifyItemRangeInserted(0,dates.size)
                true }
            popupMenu.show() }
        next.setOnClickListener {
            if (chosenDay != dates.size){ chosenDay += 1
                tvDate.text = chosenDay.toString()
                makeCalendar(chosenDay)
                rvTimesBar.scrollToPosition(0)} }
        previous.setOnClickListener {
            if (chosenDay != 1){ chosenDay -= 1
                tvDate.text = chosenDay.toString()
                makeCalendar(chosenDay)
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
        val url = "http://192.168.1.102:8012/saloon/calendar.php"
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                println(response)
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
                    val bookingId = obj.getInt("booking_id")
                    val styleId = obj.getInt("style_id")
                    if (firstSpan > 0){
                        val startHour = startBook.split(":")[0].toInt()
                        val previousItem = CalendarItem(getString(R.string.clock,startHour,0),
                            getString(R.string.clock,startHour+1,0),span=firstSpan,
                            date=getString(R.string.datetime,year,(month+1),chosenDay))
                        calendarList[timePosition-1] = previousItem }
                    for (y in 1 until removeHours+1){calendarList[timePosition+y].gone = true}
                    calendarList[timePosition+removeHours+1].span = finalSpan
                    val item = CalendarItem(startBook,endBook,"",rowCount,2,id=bookingId,styleId=styleId)
                    calendarList[timePosition] = item
                }
                for (x in 0 until breaksArray.length()) {
                    val breaks = breaksArray.getJSONObject(x)
                    val timePosition = breaks.getInt("position")
                    val rowCount = breaks.getInt("row_count")
                    val startBreak = breaks.getString("start")
                    val endBreak = breaks.getString("end")
                    val removeHours = breaks.getInt("remove_hours")
                    val firstSpan = breaks.getInt("first_span")
                    val finalSpan = breaks.getInt("final_span")
                    val breakId = breaks.getInt("id")
                    for (y in 1 until removeHours+1){ calendarList[timePosition+y].gone = true}
                    calendarList[timePosition+removeHours+1].span = finalSpan
                    val item = CalendarItem(startBreak,endBreak,"",rowCount,1,id=breakId)
                    calendarList[timePosition] = item
                    if (firstSpan > 0){
                        val startHour = startBreak.split(":")[0].toInt()
                        val previousItem = CalendarItem(getString(R.string.clock,startHour,0),
                            getString(R.string.clock,startHour+1,0),span=firstSpan,
                            date=getString(R.string.datetime,year,(month+1),chosenDay))
                        calendarList.add(timePosition,previousItem)
                    }}
                rvCalendar.adapter?.notifyItemRangeChanged(0,calendarList.size)
                rvCalendar.scrollToPosition(chosenDay)
                                                },
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

    private fun daysInAMonth( m: Int, year: Int): MutableList<String> {
       val  month = m + 1
        val daysObj = YearMonth.of(year,month)
        val days = daysObj.lengthOfMonth()
        val dateList = mutableListOf<String>()
        for (day in 1 until days){ dateList.add(getString(R.string.datetime,year,month,day)) }
        return dateList
    }


}