package com.example.saloon

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class PasswordFragment : Fragment() {

    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirm: TextInputEditText
    private lateinit var btnSavePassword: AppCompatButton
    private lateinit var accountItem: AccountItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    fun fill(){
        etPassword.setText("pass")
        etConfirm.setText("pass")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getParcelable<AccountItem>("accountItem")?.let {
            accountItem = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_password, container, false)

        etPassword = rootView.findViewById(R.id.etPassword)
        etConfirm = rootView.findViewById(R.id.etConfirm)
        btnSavePassword = rootView.findViewById(R.id.btnSavePassword)
        fill()

        btnSavePassword.setOnClickListener {
            var filled = true
            if (etPassword.text!!.isEmpty()){filled=false;etPassword.error="This field must be filled"}
            if (etConfirm.text!!.isEmpty()){filled=false;etConfirm.error="This field must be filled"}
            if (etConfirm.text == etPassword.text){filled=false;etConfirm.error="Password must be the same"}
            if (filled){
                val url = "http://192.168.1.102:8012/saloon/name.php"
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        println(response)
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
                val fm = parentFragmentManager
                fm.commit { replace(R.id.fragmentContainer,UpdateFragment.newInstance(accountItem)) }
            }
        }

        return rootView
    }

    companion object {
        fun newInstance(param1: AccountItem) =
            PasswordFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("accountItem", param1)
                }
            }
    }
}