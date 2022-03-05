package com.example.saloon

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.GeoPoint
import java.io.IOException

class RegisterLocationFragment : Fragment() {

    private lateinit var etAddress1: TextInputEditText
    private lateinit var etCity: TextInputEditText
    private lateinit var etPostcode: TextInputEditText
    private lateinit var acCountry: AutoCompleteTextView
    private lateinit var etTown: TextInputEditText
    private lateinit var btnSaveAddress: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_register_location, container, false)
        val accountItem = arguments?.getParcelable<AccountItem>("accountItem")!!
        etAddress1 = rootView.findViewById(R.id.etAddress1)
        etCity = rootView.findViewById(R.id.etCity)
        etPostcode = rootView.findViewById(R.id.etPostcode)
        acCountry = rootView.findViewById(R.id.acCountry)
        etTown = rootView.findViewById(R.id.etTown)
        btnSaveAddress = rootView.findViewById(R.id.btnSaveAddress)
        val countries = arrayListOf("England","Whales","Scotland","USA")
        acCountry.setAdapter(ArrayAdapter(requireContext(),R.layout.text_layout,countries.toTypedArray()))

        btnSaveAddress.setOnClickListener {
            var filled = true
            if (etAddress1.text!!.isEmpty()){filled=false;etAddress1.error="This field must be filled"}
            if (etCity.text!!.isEmpty()){filled=false;etCity.error="This field must be filled"}
            if (etPostcode.text!!.isEmpty()){filled=false;etPostcode.error="This field must be filled"}
            if (etTown.text!!.isEmpty()){filled=false;etTown.error="This field must be filled"}
            val addressText = getString(R.string.distance,etAddress1.text,etCity.text,acCountry.text)
            val latLong = getLocationFromAddress(addressText)
            if (filled && latLong != null){
                val addressItem = AddressItem(etCity.text.toString(), etPostcode.text.toString(),
                    acCountry.text.toString(), etAddress1.text.toString(),etTown.text.toString())
                val url = getString(R.string.url,"register.php")
                val stringRequest: StringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        accountItem.id = response
                        Toast.makeText(context,"Account created!",Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, DefaultActivity::class.java)
                        intent.putExtra("account_item", accountItem)
                        startActivity(intent) },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["address"] = addressItem.address
                        params["city"] = addressItem.city
                        params["postcode"] = addressItem.postcode
                        params["country"] = addressItem.country
                        params["town"] = addressItem.town
                        params["latitude"] = latLong.latitude.toString()
                        params["longitude"] = latLong.longitude.toString()
                        params["email"] = accountItem.email
                        params["password"] = accountItem.password!!
                        params["name"] = accountItem.name
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