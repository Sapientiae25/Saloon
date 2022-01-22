package com.example.saloon

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import java.util.*

class StyleFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private var day = 0
    private var month = 0
    private var year = 0
//    private lateinit var llDate : LinearLayout
//    private lateinit var tvDay : TextView
//    private lateinit var tvMonth : TextView
//    private lateinit var tvYear : TextView
    private lateinit var tvTime : TextView
    private lateinit var tvDate : TextView
    private lateinit var accountItem : AccountItem
    private lateinit var styleItem : StyleItem
    private lateinit var timeItem : TimeItem
    private lateinit var communicator: ChangeFragment
    private lateinit var chosenDate: String
    private lateinit var chosenDatetime: String
    private var booked = true
    private lateinit var tvBooked: TextView
    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getParcelable<AccountItem>("accountItem")?.let {
            accountItem = it }
        arguments?.getParcelable<StyleItem>("styleItem")?.let {
            styleItem = it } }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_style, container, false)

        val tvEditStyle = rootView.findViewById<TextView>(R.id.tvEditStyle)
        val tvDuration = rootView.findViewById<TextView>(R.id.tvDuration)
        val tvName = rootView.findViewById<TextView>(R.id.tvName)
        val tvInfo = rootView.findViewById<TextView>(R.id.tvInfo)
        val llReviews = rootView.findViewById<LinearLayout>(R.id.llReviews)
        val styleRating = rootView.findViewById<RatingBar>(R.id.styleRating)
        val llMoreLikeThis = rootView.findViewById<LinearLayout>(R.id.llMoreLikeThis)
        val rvMoreLike = rootView.findViewById<RecyclerView>(R.id.rvMoreLike)
        val rvReviews = rootView.findViewById<RecyclerView>(R.id.rvReviews)
        val reviewList = mutableListOf<ReviewItem>()
        tvBooked = rootView.findViewById(R.id.tvBooked)
        rvReviews.adapter = ReviewAdapter(reviewList)
        rvReviews.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
        timeItem = styleItem.time
        val timeValue = if (timeItem.maxTime != null) getString(R.string.time_distance,timeItem.time,timeItem.maxTime)
        else timeItem.time
        tvDate = rootView.findViewById(R.id.tvDate)
//        llDate = rootView.findViewById(R.id.llDate)
//        tvDay = rootView.findViewById(R.id.tvDay)
//        tvMonth = rootView.findViewById(R.id.tvMonth)
//        tvYear = rootView.findViewById(R.id.tvYear)
        tvTime = rootView.findViewById(R.id.tvTime)
        val calendar = Calendar.getInstance()
        chosenDate = getString(R.string.datetime,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,
            calendar.get(Calendar.DAY_OF_MONTH))
        tvDate.text = chosenDate
//        tvDay.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
//        tvMonth.text = (calendar.get(Calendar.MONTH)+1).toString()
//        tvYear.text = calendar.get(Calendar.YEAR).toString()


        tvDuration.text = getString(R.string.time_mins,timeValue)
        activity!!.title = styleItem.name
        tvName.text = styleItem.name
        tvInfo.text = styleItem.info
        communicator = activity as ChangeFragment
        tvEditStyle.setOnClickListener { communicator.change(EditStyleFragment.newInstance(accountItem,styleItem))}

        tvTime.setOnClickListener {showCustomDialog(tvTime); checkBooking() }
        tvDate.setOnClickListener {
            val cal: Calendar = Calendar.getInstance()
            day = cal.get(Calendar.DAY_OF_MONTH)
            month = cal.get(Calendar.MONTH)
            year = cal.get(Calendar.YEAR)
            chosenDate = getString(R.string.datetime,year,month,day)
            DatePickerDialog(context!!,this,year,month,day).show()
        }
        llReviews.setOnClickListener {
            rvReviews.visibility = if (rvReviews.visibility == View.GONE){View.VISIBLE} else {View.GONE}
        }
        val url = "http://192.168.1.102:8012/saloon/get_reviews.php"
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                println(response)
                val arr = JSONArray(response)
                var total = 0
                styleRating.rating = 0f
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val review = obj.getString("review")
                    val rating = obj.getInt("rating")
                    val reviewDate = obj.getString("date")
                    total += rating
                    reviewList.add(ReviewItem(review,rating,reviewDate))
                }
                if (reviewList.size > 0) { val average = total / reviewList.size
                    styleRating.rating = average.toFloat()
                    rvReviews.adapter?.notifyItemRangeInserted(0, reviewList.size) }
            },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["style_id"] = styleItem.id
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        checkBooking()
        return rootView
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
//        tvDay.text = day.toString()
//        tvMonth.text = (month+1).toString()
//        tvYear.text = year.toString()
        chosenDate = getString(R.string.datetime,year,(month+1),day)
        tvDate.text = chosenDate
        checkBooking() }
    companion object {
        fun newInstance(param1: AccountItem,param2 : StyleItem) =
            StyleFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("accountItem", param1)
                    putParcelable("styleItem", param2) } } }
    private fun checkBooking(){
        chosenDatetime = getString(R.string.make_datetime,tvTime.text,chosenDate)
        val url = "http://192.168.1.102:8012/saloon/check_booking_time.php"
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response -> println(response)
                if (response.toInt() == 0) { tvBooked.visibility = View.GONE; booked = true }
                else { tvBooked.visibility = View.VISIBLE;booked = false } },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["account_id"] = accountItem.id
                params["datetime"] = chosenDatetime
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest) }
    private fun showCustomDialog(textView: TextView) {
        val dialog = Dialog(context!!)
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
            minute = x.toInt() }
        close.setOnClickListener { dialog.dismiss() }
        save.setOnClickListener { println("$hour:$minute") ;dialog.dismiss()
            textView.text = getString(R.string.clock,hour,minute) }
        dialog.show()
    }
}