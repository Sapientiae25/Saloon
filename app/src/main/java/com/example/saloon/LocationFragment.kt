package com.example.saloon

import android.location.Address
import android.location.Geocoder
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
import com.google.firebase.firestore.GeoPoint
import org.json.JSONObject
import java.io.IOException
import java.util.*

class LocationFragment : Fragment() {

    private lateinit var etAddress1: TextInputEditText
    private lateinit var etCity: TextInputEditText
    private lateinit var etPostcode: TextInputEditText
    private lateinit var etCountry: TextInputEditText
    private lateinit var etTown: TextInputEditText
    private lateinit var btnSaveAddress: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_location, container, false)
        val accountItem = (activity as DefaultActivity).accountItem
        etAddress1 = rootView.findViewById(R.id.etAddress1)
        etCity = rootView.findViewById(R.id.etCity)
        etPostcode = rootView.findViewById(R.id.etPostcode)
        etCountry = rootView.findViewById(R.id.etCountry)
        etTown = rootView.findViewById(R.id.etTown)
        btnSaveAddress = rootView.findViewById(R.id.btnSaveAddress)

        var url = "http://192.168.1.102:8012/saloon/get_address.php"
        var stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                println(response)
                val obj = JSONObject(response)
                val address = obj.getString("address")
                val postcode = obj.getString("postcode")
                val country = obj.getString("country")
                val city = obj.getString("city")
                val town = obj.getString("number")

                etAddress1.setText(address)
                etCity.setText(postcode)
                etPostcode.setText(country)
                etCountry.setText(city)
                etTown.setText(town) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["account_id"] = accountItem.id
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        btnSaveAddress.setOnClickListener {
            var filled = true
            if (etAddress1.text!!.isEmpty()){filled=false;etAddress1.error="This field must be filled"}
            if (etCity.text!!.isEmpty()){filled=false;etCity.error="This field must be filled"}
            if (etPostcode.text!!.isEmpty()){filled=false;etPostcode.error="This field must be filled"}
            if (etCountry.text!!.isEmpty()){filled=false;etCountry.error="This field must be filled"}
            if (etTown.text!!.isEmpty()){filled=false;etTown.error="This field must be filled"}
            val addressText = getString(R.string.distance,etAddress1.text,etCity.text,etCountry.text)
            val latLong = getLocationFromAddress(addressText)
            if (filled && latLong != null){
                val addressItem = AddressItem(etCity.text.toString(), etPostcode.text.toString(),
                    etCountry.text.toString(), etAddress1.text.toString(),etTown.text.toString())
                url = "http://192.168.1.102:8012/saloon/address.php"
                stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        println(response)
                        Toast.makeText(context,"Address Updated!", Toast.LENGTH_SHORT).show()
                        val communicator = activity as ChangeFragment
                        communicator.change(SettingFragment())
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
                        params["latitude"] = latLong.latitude.toString()
                        params["longitude"] = latLong.longitude.toString()
                        return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
            }
        }

        return rootView
    }
    private fun getLocationFromAddress(strAddress: String?): GeoPoint? {
        val coder = Geocoder(context)
        val address: List<Address>?
        val p1: GeoPoint?
        try {
            address = coder.getFromLocationName(strAddress, 1)
            if (address == null) { return null }
            val location: Address = address[0]
            location.latitude
            location.longitude
            p1 = GeoPoint((location.latitude * 1E6), (location.longitude * 1E6))
            return p1
        } catch (e: IOException) { e.printStackTrace() }
        return null
    }
}