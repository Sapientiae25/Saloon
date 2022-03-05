package com.example.saloon

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RegisterFragment : Fragment() {
    private lateinit var etPassword: TextInputEditText
    private lateinit var etUsername: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnRegister: AppCompatButton
    private lateinit var tvLoginInstead: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_register, container, false)
        etUsername = rootView.findViewById(R.id.etUsername)
        etEmail = rootView.findViewById(R.id.etEmail)
        etPassword = rootView.findViewById(R.id.etPassword)
        btnRegister = rootView.findViewById(R.id.btnRegister)
        tvLoginInstead = rootView.findViewById(R.id.tvLoginInstead)

        btnRegister.setOnClickListener { view ->
            var filled = true
            if (etUsername.text!!.isEmpty()){filled=false;etUsername.error="This field must be filled"}
            if (etEmail.text!!.isEmpty()){filled=false;etEmail.error="This field must be filled"}
            else if (etEmail.text!!.length < 7){filled=false;etEmail.error="Password must be over 6 characters"}
            if (etPassword.text!!.isEmpty()){filled=false;etPassword.error="This field must be filled"}
            if (filled){
                val url = getString(R.string.url,"check_account_exists.php")
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        if (response == "0"){
                            val accountItem = AccountItem(name=etUsername.text.toString(),password=etPassword.text.toString(),
                                email=etEmail.text.toString())
                            val bundle = bundleOf(Pair("accountItem",accountItem))
                            view.findNavController().navigate(R.id.action_registerFragment_to_registerLocationFragment,bundle)
                        }else{
                            Toast.makeText(context,"Account already exists!", Toast.LENGTH_SHORT).show()
                        }},
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["email"] = etEmail.text.toString()
                        return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
            }
        }
        tvLoginInstead.setOnClickListener { view -> view.findNavController().navigate(R.id.action_registerFragment_to_loginFragment) }

        return rootView
    }
}