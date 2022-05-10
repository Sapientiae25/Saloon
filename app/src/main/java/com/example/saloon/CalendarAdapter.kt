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
import org.json.JSONArray
import java.time.YearMonth

class CalendarAdapter (val dateList: MutableList<Triple<Int,Int,Int>>, val accountItem: AccountItem,
                       val calendarList: MutableList<CalendarItem>, val fragment: CalendarFragment)
    : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val rvTimesBar = itemView.findViewById<RecyclerView>(R.id.rvTimesBar)
        private val rvCalendar = itemView.findViewById<RecyclerView>(R.id.rvCalendar)
        private lateinit var dates: MutableList<String>
        lateinit var timeScrollListener: RecyclerView.OnScrollListener
        lateinit var calendarScrollListener: RecyclerView.OnScrollListener
        private var year: Int= 0
        private var month: Int = 0
        private var chosenDay: Int = 0
        private val timesBarList = mutableListOf("0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16"
            ,"17","18","19","20","21","22","23")
        val calendar = mutableListOf<CalendarItem>()

        fun bind(index: Int){
            val currentItem = dateList[index]

            year = currentItem.first
            month = currentItem.second
            chosenDay = currentItem.third

            rvCalendar.layoutManager = LinearLayoutManager(itemView.context)
            rvCalendar.setHasFixedSize(true)
            rvCalendar.adapter = DayAdapter(calendar,fragment)
            rvCalendar.layoutManager?.scrollToPosition(0)

            rvTimesBar.adapter = TimesBarAdapter(timesBarList)
            rvTimesBar.setHasFixedSize(true)
            rvTimesBar.layoutManager = LinearLayoutManager(itemView.context)
            rvTimesBar.adapter?.notifyItemRangeInserted(0,timesBarList.size)
            rvTimesBar.layoutManager?.scrollToPosition(0)

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
            for (day in 1 until days+1){ dateList.add(itemView.context.getString(R.string.datetime,year,month-1,day)) }
            return dateList }

        private fun makeCalendar(index: Int){
            val currentItem = dates[index-1]
            rvCalendar.adapter?.notifyItemRangeRemoved(0,calendar.size)
            calendar.clear()

            val url = itemView.context.getString(R.string.url,"make_calendar.php")
            val stringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { response -> Log.println(Log.ASSERT,"response",response)
                    val arr = JSONArray(response)
                    val calendarItem = CalendarItem(date=currentItem)
                    var span = 1
                    var available = true
                    var bookSpan = 0
                    var new = false

                    for (z in 0 until calendarList.size){val item = calendarList[z]
                        if (bookSpan > 1){ bookSpan-=1
                            if (bookSpan == 1){
                                calendarItem.end = item.end
                                calendarItem.endMinute = item.endMinute
                                calendar.add(calendarItem.copy())
                                span = 1
                                bookSpan=0
                                new = true
                                available = true}
                        }
                        else{

                        for (x in 0 until arr.length()) {
                            val obj = arr.getJSONObject(x)
                            val startBookNull = obj.getString("start").toIntOrNull()
                            val endBookNull = obj.getString("end").toIntOrNull()
                            val calType = obj.getInt("calendar")
                            val id = obj.getInt("id")
                            val bookingId = obj.getString("booking_id")
                            if ((startBookNull != null && endBookNull != null && startBookNull >= item.startMinute
                                && startBookNull < item.endMinute)
                                || (startBookNull == null && endBookNull == null) ||
                                (endBookNull != null && startBookNull == null && endBookNull >= item.startMinute && endBookNull <
                                item.endMinute) || (startBookNull != null && endBookNull == null && startBookNull >= item.startMinute
                                && startBookNull < item.endMinute)){
                                if (available){
                                    val startBook = startBookNull ?: 0
                                    val endBook = endBookNull ?: 1440
                                    calendarItem.startMinute = item.startMinute
                                    calendarItem.start = item.start
                                    calendarItem.calendarType = calType
                                    calendarItem.id = id
                                    calendarItem.bookingId = bookingId
                                    val diff = (endBook - startBook) / 15
                                    bookSpan = if ((endBook - startBook) % 15 == 0) diff else diff + 1
                                    calendarItem.span = bookSpan
                                    available = false}
                                break } }

                        if ((item.startMinute % 60 == 0 || new) && available){
                            new = false
                            calendarItem.startMinute = item.startMinute
                            calendarItem.start = item.start
                        }
                        else if (item.endMinute % 60 == 0){
                            calendarItem.end = item.end
                            calendarItem.endMinute = item.endMinute
                            calendarItem.span = span
                            calendarItem.calendarType = 0
                            calendar.add(calendarItem.copy())
                            span = 0 }
                        span += 1
                    }}
                    rvCalendar.adapter = DayAdapter(calendar,fragment)
                    rvCalendar.adapter?.notifyItemRangeInserted(0,calendar.size) },
                Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["account_id"] = accountItem.id
                params["start"] = itemView.context.getString(R.string.make_datetime,currentItem,"00:00")
                params["end"] = itemView.context.getString(R.string.make_datetime,currentItem,"23:59")
                return params }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest)

        } }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.day_layout, parent, false)
        return CalendarViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(position) }

    override fun getItemCount() = dateList.size

}