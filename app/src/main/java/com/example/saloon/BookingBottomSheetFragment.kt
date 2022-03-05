package com.example.saloon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONObject
import java.util.*

class BookingBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_booking_bottom_sheet, container, false)
        val bookingItem = arguments?.getParcelable<BookingItem>("bookingItem")!!
        val tvName = rootView.findViewById<TextView>(R.id.tvName)
        val tvCode = rootView.findViewById<TextView>(R.id.tvCode)
        val tvCost = rootView.findViewById<TextView>(R.id.tvCost)
        val tvTime = rootView.findViewById<TextView>(R.id.tvTime)
        val btnGoToStyle = rootView.findViewById<AppCompatButton>(R.id.btnGoToStyle)
        tvName.text = bookingItem.name
        tvCode.text = getString(R.string.booking_id,bookingItem.bookingId)
        tvTime.text = getString(R.string.separate,bookingItem.start,bookingItem.end)
        tvCost.text = getString(R.string.money,bookingItem.cost.toFloat())

        btnGoToStyle.setOnClickListener { view ->
            val url = getString(R.string.url,"style_info.php")
            val stringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { response ->
                    println(response)
                    val obj = JSONObject(response)
                    val name = obj.getString("name")
                    val price = obj.getString("price").toFloat()
                    val time = obj.getString("time")
                    val styleId = obj.getString("style_id")
                    val maxTime = obj.getString("max_time")
                    val info = obj.getString("info")
                    val timeItem = TimeItem(time,maxTime)
                    val styleItem = StyleItem(name,price,timeItem,info,styleId)
                    val bundle = bundleOf(Pair("styleItem",styleItem))
                    view.findNavController().navigate(R.id.action_bookingFragment_to_styleFragment,bundle) },
                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["style_id"] = bookingItem.styleId
                    return params
                }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest)
        }
        return rootView
    }
}