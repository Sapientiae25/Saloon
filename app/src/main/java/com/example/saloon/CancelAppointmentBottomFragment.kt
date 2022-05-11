package com.example.saloon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText

class CancelAppointmentBottomFragment(val listener: () -> Unit) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.bottom_fragment_cancel_appointment, container, false)
        val styleItem = arguments?.getParcelable<StyleItem>("styleItem")!!
        val email = arguments?.getString("email")!!
        val timePeriod = arguments?.getString("timePeriod")!!
        val accountId = arguments?.getString("account_id")!!
        val tvStyleName = rootView.findViewById<TextView>(R.id.tvStyleName)
        val tvStyleTime = rootView.findViewById<TextView>(R.id.tvStyleTime)
        val llEmail = rootView.findViewById<LinearLayout>(R.id.llEmail)
        val etReason = rootView.findViewById<TextInputEditText>(R.id.etReason)
        val tvEmail = rootView.findViewById<TextView>(R.id.tvEmail)
        val blockBtn = rootView.findViewById<CheckBox>(R.id.blockBtn)
        val cancelBooking = rootView.findViewById<AppCompatButton>(R.id.cancelBooking)
        tvStyleName.text = styleItem.name
        tvEmail.text = email
        tvStyleTime.text = timePeriod


        cancelBooking.setOnClickListener {

            val url = getString(R.string.url,"delete_booking.php")
            val stringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { response -> Log.println(Log.ASSERT,"response",response)
                    Toast.makeText(context,"Appointment deleted",Toast.LENGTH_SHORT).show()
                    listener()
                    dismiss() },
                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["booking_id"] = styleItem.bookingId
                    params["reason"] = etReason.text.toString()
                    params["account_id"] = accountId
                    return params }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest)
        }
        return rootView
    }
}