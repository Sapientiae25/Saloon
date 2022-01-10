package com.example.saloon

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.fragment.navArgs
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class LocationFragment : Fragment() {

    private lateinit var etAddress1: TextInputEditText
    private lateinit var etCity: TextInputEditText
    private lateinit var etPostcode: TextInputEditText
    private lateinit var etCountry: TextInputEditText
    private lateinit var btnSaveAddress: Button
    private lateinit var accountItem: AccountItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    fun fill(){
        etAddress1.setText("6 Rochester Close")
        etCity.setText("London")
        etPostcode.setText("SW16 5DL")
        etCountry.setText("England")
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
        val rootView =  inflater.inflate(R.layout.fragment_location, container, false)

        etAddress1 = rootView.findViewById(R.id.etAddress1)
        etCity = rootView.findViewById(R.id.etCity)
        etPostcode = rootView.findViewById(R.id.etPostcode)
        etCountry = rootView.findViewById(R.id.etCountry)
        btnSaveAddress = rootView.findViewById(R.id.btnSaveAddress)
        fill()

        btnSaveAddress.setOnClickListener {
            var filled = true
            if (etAddress1.text!!.isEmpty()){filled=false;etAddress1.error="This field must be filled"}
            if (etCity.text!!.isEmpty()){filled=false;etCity.error="This field must be filled"}
            if (etPostcode.text!!.isEmpty()){filled=false;etPostcode.error="This field must be filled"}
            if (etCountry.text!!.isEmpty()){filled=false;etCountry.error="This field must be filled"}
            if (filled){
                val addressItem = AddressItem(etCity.text.toString(), etPostcode.text.toString(),
                    etCountry.text.toString(), etAddress1.text.toString())
                val url = "http://192.168.1.102:8012/saloon/address.php"
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        println(response)
                        println(addressItem)
                        accountItem.addressItem = addressItem
                        Toast.makeText(context,"Address Updated!", Toast.LENGTH_SHORT).show()
                        val fm = parentFragmentManager
                        fm.commit { replace(R.id.fragmentContainer,UpdateFragment.newInstance(accountItem)) }
                    },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["address"] = addressItem.address
                        params["city"] = addressItem.city
                        params["postcode"] = addressItem.postcode
                        params["country"] = addressItem.country
                        params["account_id"] = accountItem.id
                        return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
            }
        }

        return rootView
    }

    companion object {
        fun newInstance(param1: AccountItem) =
            LocationFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("accountItem", param1)
                }
            }
    }
}