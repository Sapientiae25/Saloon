package com.example.saloon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.commit
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.util.HashMap

class SaloonNameFragment : Fragment() {

    private lateinit var etName: TextInputEditText
    private lateinit var btnSaveNumber: Button
    private lateinit var accountItem: AccountItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    fun fill(){
        etName.setText("Sapientiae")
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
        val rootView =  inflater.inflate(R.layout.fragment_saloon_name, container, false)

        etName = rootView.findViewById(R.id.etName)
        btnSaveNumber = rootView.findViewById(R.id.btnSaveAddress)
        fill()

        btnSaveNumber.setOnClickListener {
            var filled = true
            if (etName.text!!.isEmpty()){filled=false;etName.error="This field must be filled"}
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
                        params["name"] = etName.text.toString()
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
            SaloonNameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("accountItem", param1)
                }
            }
    }
}