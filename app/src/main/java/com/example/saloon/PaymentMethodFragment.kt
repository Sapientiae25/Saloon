package com.example.saloon

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.textfield.TextInputEditText

class PaymentMethodFragment : Fragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_payment_method, container, false)
        (activity as DefaultActivity).supportActionBar?.title = "Payment"
        val accountItem = (activity as DefaultActivity).accountItem
        val etCVV = rootView.findViewById<TextInputEditText>(R.id.etCVV)
        val etExpiry = rootView.findViewById<TextInputEditText>(R.id.etExpiry)
        val etNumber = rootView.findViewById<TextInputEditText>(R.id.etNumber)
        val btnSave = rootView.findViewById<TextInputEditText>(R.id.btnSave)

        etExpiry.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                if (etExpiry.text?.length == 2){etExpiry.setText(getString(R.string.expiry_format,etExpiry.text))}

                return@OnKeyListener true
            }
            false
        })

        btnSave.setOnClickListener {view ->
            var filled = true
            val expire = etExpiry.text
            if (etCVV.text?.length != 3) {filled = false; etCVV.error = "Please Fill This Field Out Correctly"}
            if (etNumber.text?.length != 16) {filled = false; etNumber.error = "Please Fill This Field Out Correctly"}
            if (expire?.length != 5) {filled = false; etExpiry.error = "Please Fill This Field Out Correctly"}
            if (expire?.substring(0,2)?.toInt()!! > 13 || expire.substring(2,4).toInt() > 31) {filled = false
                etExpiry.error = "Please Fill This Field Out Correctly"}
            if (filled){
                val url = getString(R.string.url,"add_payment.php")
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = java.util.HashMap<String, String>()
                        params["number"] = etNumber.text.toString()
                        params["cvv"] = etCVV.text.toString()
                        params["expiry"] = etExpiry.text.toString()
                        params["saloon_fk"] = accountItem.id
                        return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
                (activity as DefaultActivity).hasPayment = true
                view.findNavController().popBackStack()
            }
        }

        return rootView
    }
}
