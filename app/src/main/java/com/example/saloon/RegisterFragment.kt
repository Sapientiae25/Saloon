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
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
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

    fun fill(){
        etUsername.setText("Sapientiae")
        etEmail.setText("test@gmail.com")
        etPassword.setText("pass")
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
        fill()

        btnRegister.setOnClickListener {
            var filled = true
            if (etUsername.text!!.isEmpty()){filled=false;etUsername.error="This field must be filled"}
            if (etEmail.text!!.isEmpty()){filled=false;etEmail.error="This field must be filled"}
            if (etPassword.text!!.isEmpty()){filled=false;etPassword.error="This field must be filled"}
            if (filled){
                val url = "http://192.168.1.102:8012/saloon/register.php"
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        val obj = JSONObject(response)
                        val exist = obj.getInt("exist")
                        if (exist == 0){
                            Toast.makeText(context,"Account created!",Toast.LENGTH_SHORT).show()
                            val accountId = obj.getString("account_id")
                            val intent = Intent(context, DefaultActivity::class.java)
                            val accountItem = AccountItem(accountId,etUsername.text.toString())
                            intent.putExtra("account_item", accountItem)
                            startActivity(intent)
                        }else{
                            Toast.makeText(context,"Account already exists!", Toast.LENGTH_SHORT).show()
                        }},
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["email"] = etEmail.text.toString()
                        params["password"] = etPassword.text.toString()
                        params["name"] = etUsername.text.toString()
                        return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
            }
        }

        tvLoginInstead.setOnClickListener {
            val fm = parentFragmentManager
            fm.commit {
                replace(R.id.fragmentContainer,LoginFragment())
            }
        }

        return rootView
    }

//    companion object {
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            RegisterFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}