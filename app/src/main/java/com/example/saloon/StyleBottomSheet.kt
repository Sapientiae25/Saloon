package com.example.saloon

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.math.max

class StyleBottomSheet : BottomSheetDialogFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.style_bottom_sheet_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val booking = arguments?.getParcelable<CalendarItem>("booking")!!
        val accountItem = (activity as DefaultActivity).accountItem
        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvCost = view.findViewById<TextView>(R.id.tvCost)
        val tvStyleDuration = view.findViewById<TextView>(R.id.tvStyleDuration)
        val tvTimePeriod = view.findViewById<TextView>(R.id.tvTimePeriod)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val ivImage = view.findViewById<ImageView>(R.id.ivImage)
        val removeBtn = view.findViewById<AppCompatButton>(R.id.removeBtn)
        tvTimePeriod.text = getString(R.string.obj_colon,"Time",getString(R.string.time_distance,booking.start,booking.end))
        val url = getString(R.string.url,"style_info.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                println(response)
                val obj = JSONObject(response)
                val name = obj.getString("name")
                val price = obj.getString("price").toFloat()
                val time = obj.getString("time")
                val bookingId = obj.getString("booking_id")
                val maxTime = obj.getString("max_time")
                val email = obj.getString("email")
                val timeItem = TimeItem(time,maxTime)
                tvEmail.text = email
                tvName.text = name
                tvCost.text = getString(R.string.obj_colon,"Cost",getString(R.string.money,price))
                val timeValue = if (maxTime.isNotEmpty()) getString(R.string.time_distance,time,maxTime) else time
                tvStyleDuration.text = getString(R.string.obj_colon,"Duration",getString(R.string.time_mins,timeValue))
                val styleItem = StyleItem(name,price,timeItem,id=booking.styleId.toString(), bookingId=bookingId)
                removeBtn.setOnClickListener{
                    val intent = Intent(context, CancelAppointmentActivity::class.java)
                    intent.putExtra("styleItem", styleItem)
                    intent.putExtra("email", email)
                    intent.putExtra("timePeriod", tvTimePeriod.text)
                    startActivity(intent) }},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["style_id"] = booking.styleId.toString()
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

}