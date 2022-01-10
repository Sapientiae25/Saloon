package com.example.saloon

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Float.parseFloat
import java.util.*

class StyleActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_style)
        val accountItem = intent.getParcelableExtra<AccountItem>("account_item")
        val styleItem = intent.getParcelableExtra<StyleItem>("style_item")
        val tvUserView = findViewById<TextView>(R.id.tvUserView)
        val tvEditStyle = findViewById<TextView>(R.id.tvEditStyle)

        supportFragmentManager.commit { add(R.id.fragmentContainer,StyleFragment.newInstance(accountItem!!,styleItem!!))}

        tvUserView.setOnClickListener {
            supportFragmentManager.commit { replace(R.id.fragmentContainer,StyleFragment.newInstance(accountItem!!,styleItem!!))}
        }
        tvEditStyle.setOnClickListener {
            supportFragmentManager.commit { replace(R.id.fragmentContainer,EditStyleFragment.newInstance(accountItem!!,styleItem!!))}
        }

    }}

//        accountItem = intent.getParcelableExtra("account_item")!!
//        val styleItem = intent.getParcelableExtra<StyleItem>("style_item")
//        val tvDuration = findViewById<TextView>(R.id.tvDuration)
//        val tvName = findViewById<TextView>(R.id.tvName)
//        val tvInfo = findViewById<TextView>(R.id.tvInfo)
//        val llReviews = findViewById<LinearLayout>(R.id.llReviews)
//        val styleRating = findViewById<RatingBar>(R.id.styleRating)
//        val llMoreLikeTHis = findViewById<LinearLayout>(R.id.llMoreLikeTHis)
//        val rvMoreLike = findViewById<RecyclerView>(R.id.rvMoreLike)
//        val rvReviews = findViewById<RecyclerView>(R.id.rvReviews)
//        val reviewList = mutableListOf<ReviewItem>()
//        rvReviews.adapter = ReviewAdapter(reviewList)
//        rvReviews.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
//        timeItem = styleItem!!.time
//        val timeValue = if (timeItem.maxTime != null) getString(R.string.time_distance,timeItem.time,timeItem.maxTime)
//        else timeItem.time
//        llDate = findViewById(R.id.llDate)
//        tvDay = findViewById(R.id.tvDay)
//        tvMonth = findViewById(R.id.tvMonth)
//        tvYear = findViewById(R.id.tvYear)
//        tvTime = findViewById(R.id.tvTime)

//        tvDuration.text = getString(R.string.time_mins,timeValue)
//        tvName.text = accountItem.name
//        tvInfo.text = styleItem.info
//
//
//        tvTime.setOnClickListener {
//            if (timeList.isEmpty()){
//                Toast.makeText(this, "Please decide a date first",Toast.LENGTH_SHORT).show() }
//            else{
//                val popupMenu = PopupMenu(this,tvTime)
//                popupMenu.inflate(R.menu.time_menu)
//                for (i in 0 until timeList.size){
//                    val time = timeList[i]
//                    popupMenu.menu.add(0,i,i,time)}
//                popupMenu.setOnMenuItemClickListener { item ->
//                    tvTime.text = timeList[item.itemId]
//                    true
//                }
//                popupMenu.show() } }
//        llDate.setOnClickListener {
//            val cal: Calendar = Calendar.getInstance()
//            day = cal.get(Calendar.DAY_OF_MONTH)
//            month = cal.get(Calendar.MONTH)
//            year = cal.get(Calendar.YEAR)
//            DatePickerDialog(this,this,year,month,day).show()
//        }
//        llReviews.setOnClickListener {
//            rvReviews.visibility = if (rvReviews.visibility == View.GONE){View.VISIBLE} else {View.GONE}
//        }
//        val url = "http://192.168.1.102:8012/saloon/get_reviews.php"
//        val stringRequest = object : StringRequest(
//            Method.POST, url, Response.Listener { response ->
//                val arr = JSONArray(response)
//                var total = 0
//                for (x in 0 until arr.length()){
//                    val obj = arr.getJSONObject(x)
//                    val review = obj.getString("review")
//                    val rating = obj.getInt("rating")
//                    val reviewDate = obj.getString("date")
//                    total += rating
//                    reviewList.add(ReviewItem(review,rating,reviewDate))
//                }
//                val average = total / reviewList.size
//                styleRating.rating = average.toFloat()
//                rvReviews.adapter?.notifyItemRangeInserted(0,reviewList.size)
//            },
//            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
//            @Throws(AuthFailureError::class)
//            override fun getParams(): Map<String, String> {
//                val params = HashMap<String, String>()
//                params["style_id"] = styleItem.id
//                return params
//            }}
//        VolleySingleton.instance?.addToRequestQueue(stringRequest)

//    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
//        val timeText = getString(R.string.clock,hour,minute)
//        tvTime.text = timeText
//    }
//
//    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
//        tvDay.text = day.toString()
//        tvMonth.text = (month+1).toString()
//        tvYear.text = year.toString()
//        val datetime = getString(R.string.datetime,year,(month+1),day)
//        timeList.clear()
//
//        val url = "http://192.168.1.102:8012/saloon/booking_time.php"
//        val stringRequest = object : StringRequest(
//            Method.POST, url, Response.Listener { response ->
//                val obj = JSONObject(response)
//                val openList = obj.getString("open").split(":")
//                val openTime = if (openList.size > 1){ TimeObject(openList[0].toInt(), openList[1].toInt()) }else TimeObject(0,0)
//                val closeList = obj.getString("close").split(":")
//                val closeTime = if (closeList.size > 1) { TimeObject(closeList[0].toInt(), closeList[1].toInt()) }else TimeObject(0,0)
//                val timePeriod = makeTimeList(openTime,closeTime)
//                val dates = obj.getJSONArray("dates")
//                val max = if (timeItem.maxTime == null){stringToFloat(timeItem.time)} else{stringToFloat(timeItem.maxTime!!)}
//                for (x in 0 until dates.length()) {
//                    val date = dates.getJSONObject(x)
//                    val startDate = date.getString("start").replace(":",".").toFloat()
//                    val endDate = date.getString("end").replace(":",".").toFloat()
//                    // TODO Make it work for dates going into next day
//                    for (time in timePeriod){
//                        val timeFloat = parseFloat(time.hour.toString() + '.' + time.minute.toString())
//                        if ((startDate < timeFloat && timeFloat < endDate) || (timeFloat+max > startDate && timeFloat+max < endDate)){
//                            timePeriod.remove(time)
//                        }
//                    }
//                }
//                for (validTime in timePeriod){timeList.add(timeToString(validTime))}
//                                                },
//            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
//            @Throws(AuthFailureError::class)
//            override fun getParams(): Map<String, String> {
//                val params = HashMap<String, String>()
//                params["account_id"] = accountItem.id
//                params["date"] = datetime
//                return params
//            }}
//        VolleySingleton.instance?.addToRequestQueue(stringRequest)
//    }
//    fun timeToString(timeObject: TimeObject) = "${timeObject.hour}:${timeObject.minute}"
//    fun makeTimeList(start: TimeObject,end: TimeObject): MutableList<TimeObject> {
//        val timeList = mutableListOf<TimeObject>()
//        for (x in start.hour until end.hour){
//            val endMinute = if (x == end.hour){end.minute}else{60}
//            val beginMinute = if (x == start.hour){start.minute}else{0}
//            for (i in beginMinute until endMinute step(15)){
//                timeList.add(TimeObject(x,i))
//            }
//        }
//        return timeList }
//    fun stringToFloat(str: String) = str.replace(':','.').toFloat()

