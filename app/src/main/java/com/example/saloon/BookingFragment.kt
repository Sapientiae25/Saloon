package com.example.saloon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import java.util.*

class BookingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_booking, container, false)
        (activity as DefaultActivity).clearNotification()
        val accountItem = (activity as DefaultActivity).accountItem
        val tvNoBookings = rootView.findViewById<TextView>(R.id.tvNoBookings)
        val rvBookings = rootView.findViewById<RecyclerView>(R.id.rvBookings)
        val bookingList = mutableListOf<BookingItem>()
        (activity as DefaultActivity).supportActionBar?.title = "Bookings"
        rvBookings.layoutManager = LinearLayoutManager(context)
        rvBookings.adapter = BookingAdapter(bookingList)
        val url = getString(R.string.url,"get_bookings.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                Log.println(Log.ASSERT,"response",response.toString())
                val arr = JSONArray(response)
                for (i in 0 until arr.length()){
                    val obj = arr.getJSONObject(i)
                    val name = obj.getString("name")
                    val start = obj.getString("start")
                    val end = obj.getString("end")
                    val price = obj.getString("price")
                    val duration = obj.getString("duration")
                    val styleId = obj.getString("style_id")
                    val bookingId = obj.getString("booking_id")
                    val email = obj.getString("email")
                    bookingList.add(BookingItem(bookingId,start,end,name,price,duration,email,styleId)) }
                if (arr.length() == 0){tvNoBookings.visibility = View.VISIBLE}
                else{rvBookings.adapter?.notifyItemRangeInserted(0,bookingList.size)} },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["account_id"] = accountItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        return rootView
    }
}