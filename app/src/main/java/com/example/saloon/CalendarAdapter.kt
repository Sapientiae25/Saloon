package com.example.saloon

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import java.time.YearMonth

class CalendarAdapter (val dateList: MutableList<Triple<Int,Int,Int>>, val accountItem: AccountItem)
    : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val rvTimesBar = itemView.findViewById<RecyclerView>(R.id.rvTimesBar)
        private val rvCalendar = itemView.findViewById<RecyclerView>(R.id.rvCalendar)
        private lateinit var dates: MutableList<String>
        val calendarList =  mutableListOf<CalendarItem>()
        lateinit var timeScrollListener: RecyclerView.OnScrollListener
        lateinit var calendarScrollListener: RecyclerView.OnScrollListener
        var year: Int= 0
        var month: Int = 0
        var chosenDay: Int = 0
        private val timesBarList = mutableListOf<String>()

        fun bind(index: Int){
            val currentItem = dateList[index]

            year = currentItem.first
            month = currentItem.second
            chosenDay = currentItem.third

            rvCalendar.layoutManager = LinearLayoutManager(itemView.context)
            rvCalendar.setHasFixedSize(true)
            rvCalendar.adapter = TestAdapter(calendarList)
            rvCalendar.scrollToPosition(0)

            rvTimesBar.adapter = TimesBarAdapter(timesBarList)
            rvTimesBar.setHasFixedSize(true)
            rvTimesBar.layoutManager = LinearLayoutManager(itemView.context)
            rvTimesBar.adapter?.notifyItemRangeInserted(0,timesBarList.size)
            rvTimesBar.scrollToPosition(0)

            dates = daysInAMonth(month,year)
            makeCalendar(chosenDay)

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
        }
        private fun daysInAMonth( m: Int, year: Int): MutableList<String> {
            val  month = m + 1
            val daysObj = YearMonth.of(year,month)
            val days = daysObj.lengthOfMonth()
            val dateList = mutableListOf<String>()
            for (day in 1 until days+1){ dateList.add(itemView.context.getString(R.string.datetime,year,month,day)) }
            return dateList }

        private fun makeCalendar(index: Int){
            val currentItem = dates[index-1]

            calendarList.clear()
            for (h in 0 until 24){
                calendarList.add(CalendarItem(itemView.context.getString(R.string.clock,h,0),
                    itemView.context.getString(R.string.clock,h+1,0),date=itemView.context.
                    getString(R.string.datetime,year,month,chosenDay)))
                timesBarList.add(itemView.context.getString(R.string.clock,h+1,0)) }
            val url = itemView.context.getString(R.string.url,"calendar.php")
            val stringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { response ->
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
                            val previousItem = CalendarItem(itemView.context.getString(R.string.clock,startHour,0),
                                itemView.context.getString(R.string.clock,startHour+1,0),span=firstSpan,
                                date=itemView.context.getString(R.string.datetime,year,month,chosenDay))
                            calendarList[timePosition-1] = previousItem }
                        for (y in 1 until removeHours+1){calendarList[timePosition+y].gone = true}
                        calendarList[timePosition+removeHours+1].span = finalSpan
                        val item = CalendarItem(startBook,endBook,span=rowCount,calendarType=2,date=calendarDate,
                            id=bookingId,styleId=styleId)
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
                            val previousItem = CalendarItem(itemView.context.getString(R.string.clock,startHour,0),
                                itemView.context.getString(R.string.clock,startHour+1,0),span=firstSpan,
                                date=itemView.context.getString(R.string.datetime,year,month,chosenDay))
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
            VolleySingleton.instance?.addToRequestQueue(stringRequest) }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.test_rv,
            parent, false)
        return CalendarViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) { holder.bind(position) }

    override fun getItemCount() = dateList.size

}