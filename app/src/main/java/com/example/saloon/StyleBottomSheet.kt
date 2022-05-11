package com.example.saloon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.core.text.isDigitsOnly
import androidx.navigation.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import org.json.JSONObject

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
        val image = view.findViewById<ImageView>(R.id.image)
        val removeBtn = view.findViewById<AppCompatButton>(R.id.removeBtn)

        tvTimePeriod.text = getString(R.string.separate,"Time",getString(R.string.time_distance,booking.start,booking.end))
        val url = getString(R.string.url,"style_info.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response -> Log.println(Log.ASSERT,"response",response)
                val obj = JSONObject(response)
                val name = obj.getString("name")
                val price = obj.getString("price").toFloat()
                val time = obj.getString("time")
                val maxTime = obj.getString("max_time")
                val email = obj.getString("email")
                val imageId = obj.getString("image_id")
                val timeItem = TimeItem(time,maxTime)
                tvEmail.text = email
                tvName.text = name
                tvCost.text = getString(R.string.obj_colon,"Cost",getString(R.string.money,price))
                val timeValue = if (maxTime.isNotEmpty()) getString(R.string.time_distance,time,maxTime) else time
                tvStyleDuration.text = getString(R.string.obj_colon,"Duration",getString(R.string.time_mins,timeValue))
                val styleItem = StyleItem(name,price,timeItem,id=booking.id.toString(), bookingId=booking.bookingId)

                if (!imageId.isDigitsOnly()){
                    image.visibility = View.GONE
                }else{
                    Picasso.get().load(getString(
                        R.string.url,"style_images/$imageId.jpeg")).fit().centerCrop().into(image)}
                removeBtn.setOnClickListener{
                    val bundle = bundleOf(Pair("styleItem", styleItem),Pair("email", email),Pair("timePeriod", tvTimePeriod.text),
                        Pair("account_id", accountItem.id))
                    (activity as DefaultActivity).findNavController(R.id.activityFragment).navigate(R.id.
                    action_global_cancelAppointmentFragment,bundle)
                    dismiss()}},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["booking_id"] = booking.bookingId
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

}