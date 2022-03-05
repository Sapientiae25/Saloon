package com.example.saloon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class PasswordFragment : Fragment() {

    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirm: TextInputEditText
    private lateinit var btnSavePassword: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_password, container, false)
        (activity as DefaultActivity).supportActionBar?.title = "Password"
        val accountItem = (activity as DefaultActivity).accountItem
        etPassword = rootView.findViewById(R.id.etPassword)
        etConfirm = rootView.findViewById(R.id.etConfirm)
        btnSavePassword = rootView.findViewById(R.id.btnSavePassword)

        btnSavePassword.setOnClickListener {
            var filled = true
            if (etPassword.text!!.isEmpty()){filled=false;etPassword.error="This field must be filled"}
            if (etConfirm.text!!.isEmpty()){filled=false;etConfirm.error="This field must be filled"}
            if (etConfirm.text == etPassword.text){filled=false;etConfirm.error="Password must be the same"}
            if (filled){
                val url = getString(R.string.url,"name.php")
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        println(response)
                        val communicator = activity as ChangeFragment
                        communicator.change(SettingFragment())
                    },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["password"] = etConfirm.text.toString()
                        params["account_id"] = accountItem.id
                        return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
                Toast.makeText(context,"Saloon Name Updated!", Toast.LENGTH_SHORT).show()
            }
        }

        return rootView
    }
}