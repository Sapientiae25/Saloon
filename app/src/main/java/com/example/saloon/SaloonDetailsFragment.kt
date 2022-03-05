package com.example.saloon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.util.HashMap

class SaloonDetailsFragment : Fragment() {

    private lateinit var etName: TextInputEditText
    private lateinit var btnSaveName: AppCompatButton
    private lateinit var etNumber: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_saloon_details, container, false)
        (activity as DefaultActivity).supportActionBar?.title = "Details"
        val accountItem = (activity as DefaultActivity).accountItem
        etName = rootView.findViewById(R.id.etName)
        etNumber = rootView.findViewById(R.id.etNumber)
        btnSaveName = rootView.findViewById(R.id.btnSaveName)
        var url = getString(R.string.url,"get_name.php")
        var stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                println(response)
                val obj = JSONObject(response)
                val name = obj.getString("name")
                val number = obj.getString("number")
                etName.setText(name)
                etNumber.setText(number) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["account_id"] = accountItem.id
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        btnSaveName.setOnClickListener {
            var filled = true
            if (etName.text!!.isEmpty()){filled=false;etName.error="This field must be filled"}
            if (filled){
                url = getString(R.string.url,"name.php")
                stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        println(response)
                        val communicator = activity as ChangeFragment
                        communicator.change(SettingFragment())
                    },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["name"] = etName.text.toString()
                        params["account_id"] = accountItem.id
                        return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
                Toast.makeText(context,"Saloon Name Updated!", Toast.LENGTH_SHORT).show() } }

        return rootView
    }
}