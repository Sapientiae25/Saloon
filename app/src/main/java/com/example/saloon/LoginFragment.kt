package com.example.saloon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class LoginFragment : Fragment() {

    private lateinit var etPassword: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegisterAccount: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_login, container, false)
        etEmail = rootView.findViewById(R.id.etEmail)
        etPassword = rootView.findViewById(R.id.etPassword)
        btnLogin = rootView.findViewById(R.id.btnLogin)
        tvRegisterAccount = rootView.findViewById(R.id.tvRegisterAccount)
        etEmail.setText("test@gmail.com")
        etPassword.setText("pass")

        tvRegisterAccount.setOnClickListener { view -> view.findNavController().navigate(R.id.action_loginFragment_to_registerFragment) }

        btnLogin.setOnClickListener {
            var filled = true
            if (etEmail.text!!.isEmpty()){filled=false;etEmail.error="This field must be filled"}
            if (etPassword.text!!.isEmpty()){filled=false;etPassword.error="This field must be filled"}
            if (filled){
                val url = getString(R.string.url,"login.php")
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        Log.println(Log.ASSERT,"acc",response)
                        val obj = JSONObject(response)
                        val exist = obj.getInt("exist")
                        if (exist == 1){
                            val name = obj.getString("name")
                            val accountId = obj.getString("account_id")
                            val address = obj.getString("address")
                            val postcode = obj.getString("postcode")
                            val open = obj.getString("open").take(5)
                            val close = obj.getString("close").take(5)
                            val rating = obj.getString("rating")
                            val addressItem = AddressItem("",postcode,"",address,"" )
                            val accountItem = AccountItem(accountId,name,open=open,close=close,addressItem=addressItem,rating=rating)
                            val intent = Intent(context, DefaultActivity::class.java)
                            intent.putExtra("account_item", accountItem)
                            startActivity(intent)
                        }else{
                            Toast.makeText(context,"Email or Password are incorrect",Toast.LENGTH_SHORT).show()
                        }},
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["email"] = etEmail.text.toString()
                        params["password"] = etPassword.text.toString()
                        return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
            }
        }
        return rootView
    }
}