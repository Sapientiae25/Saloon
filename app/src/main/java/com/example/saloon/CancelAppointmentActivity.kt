package com.example.saloon

import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.textfield.TextInputEditText

class CancelAppointmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancel_appointment)
        val styleItem = intent.getParcelableExtra<StyleItem>("styleItem")!!
        val email = intent.getStringExtra("email")!!
        val timePeriod = intent.getStringExtra("timePeriod")!!
        val accountId = intent.getStringExtra("account_id")!!
        val tvStyleName = findViewById<TextView>(R.id.tvStyleName)
        val tvStyleTime = findViewById<TextView>(R.id.tvStyleTime)
        val llEmail = findViewById<LinearLayout>(R.id.llEmail)
        val etReason = findViewById<TextInputEditText>(R.id.etReason)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val blockBtn = findViewById<CheckBox>(R.id.blockBtn)
        val cancelBooking = findViewById<AppCompatButton>(R.id.cancelBooking)
        tvStyleName.text = styleItem.name
        tvEmail.text = email
        tvStyleTime.text = timePeriod

        cancelBooking.setOnClickListener {
            Log.println(Log.ASSERT,"shit","$accountId ${styleItem.id}")
            val url = getString(R.string.url,"delete_booking.php")
            val stringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { response -> Log.println(Log.ASSERT,"response",response); finish() },
                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["booking_id"] = styleItem.id
                    params["reason"] = etReason.text.toString()
                    params["account_id"] = accountId
                    return params
                }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest)
        }
    }
}