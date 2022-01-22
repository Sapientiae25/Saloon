package com.example.saloon

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.util.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

class LoginFragment : Fragment() {

    private lateinit var etPassword: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegisterAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    fun fill(){
        etEmail.setText("test@gmail.com")
        etPassword.setText("pass")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_login, container, false)
        etEmail = rootView.findViewById(R.id.etEmail)
        etPassword = rootView.findViewById(R.id.etPassword)
        btnLogin = rootView.findViewById(R.id.btnLogin)
        tvRegisterAccount = rootView.findViewById(R.id.tvRegisterAccount)

        tvRegisterAccount.setOnClickListener {
            val fm = parentFragmentManager
            fm.commit {
                replace(R.id.fragmentContainer,RegisterFragment())
            }
        }
        fill()

        btnLogin.setOnClickListener {
            var filled = true
            if (etEmail.text!!.isEmpty()){filled=false;etEmail.error="This field must be filled"}
            if (etPassword.text!!.isEmpty()){filled=false;etPassword.error="This field must be filled"}
            if (filled){
                val url = "http://192.168.1.102:8012/saloon/login.php"
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        val obj = JSONObject(response)
                        val exist = obj.getInt("exist")
                        if (exist == 1){
                            val name = obj.getString("name")
                            val accountId = obj.getString("account_id")
                            val accountItem = AccountItem(accountId,name)
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