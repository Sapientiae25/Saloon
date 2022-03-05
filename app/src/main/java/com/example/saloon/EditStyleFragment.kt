package com.example.saloon

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class EditStyleFragment : Fragment(){

    private lateinit var styleItem : StyleItem
    private lateinit var etDuration: AutoCompleteTextView
    var minute = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_edit_style, container, false)
        val accountItem = (activity as DefaultActivity).accountItem
        styleItem = arguments?.getParcelable("styleItem") !!
        (activity as DefaultActivity).supportActionBar?.title = styleItem.name
        val tvUserView = rootView.findViewById<TextView>(R.id.tvUserView)
        val etName = rootView.findViewById<TextInputEditText>(R.id.etName)
        val etPrice = rootView.findViewById<TextInputEditText>(R.id.etPrice)
        val tvAddImage = rootView.findViewById<TextView>(R.id.tvAddImage)
        val btnCreateStyle = rootView.findViewById<Button>(R.id.btnCreateStyle)
        etDuration = rootView.findViewById(R.id.etDuration)
        val etInfo = rootView.findViewById<TextInputEditText>(R.id.etInfo)
        val tvGender = rootView.findViewById<TextView>(R.id.tvGender)
        val tvLength = rootView.findViewById<TextView>(R.id.tvLength)
        val rgGender = rootView.findViewById<RadioGroup>(R.id.rgGender)
        val rgLength = rootView.findViewById<RadioGroup>(R.id.rgLength)

        etName.setText(styleItem.name)
        etPrice.setText(styleItem.price.toString())
        val timeItem = styleItem.time
        etDuration.setText(timeItem.time)
        btnCreateStyle.setOnClickListener {
            var filled = true
            if (etName.text!!.isEmpty()){filled=false;etName.error="This field must be filled"}
            if (etPrice.text!!.isEmpty()){filled=false;etPrice.error="This field must be filled"}
            if (minute == 0){filled=false;etDuration.error="This field must be filled"}
            val genderId: Int = rgGender.checkedRadioButtonId
            val genderButton: View = rgGender.findViewById(genderId)
            var gender = rgGender.indexOfChild(genderButton)
            val lengthId: Int = rgLength.checkedRadioButtonId
            val lengthButton: View = rgLength.findViewById(lengthId)
            var length = rgLength.indexOfChild(lengthButton)
            if (length == 0) {length = rgLength.childCount-1} else if (length == rgLength.childCount-1){length = 0}
            if (gender == 0) {gender = rgGender.childCount-1} else if (gender ==  rgGender.childCount-1){gender = 0}
            if (filled){
                val url = getString(R.string.url,"update_style.php")
                val stringRequest: StringRequest = object : StringRequest(
                    Method.GET, url, Response.Listener { response ->
                        val obj = JSONObject(response)
                        val exist = obj.getInt("exist")
                        if (exist == 1){
                            Toast.makeText(context, "Style already exists",Toast.LENGTH_SHORT).show()
                        }else{
                            tvUserView.setOnClickListener{view ->
                                val bundle = bundleOf(Pair("styleItem",styleItem))
                                view.findNavController().navigate(R.id.action_styleFragment_to_editStyleFragment,bundle) } } },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["name"] = etName.text.toString()
                        params["price"] = etPrice.text.toString()
                        params["time"] = minute.toString()
                        params["account_id"] = accountItem.id
                        params["info"] = etInfo.text.toString()
                        params["style_id"] = styleItem.id
                        params["gender"] = gender.toString()
                        params["length"] = length.toString()
                        return params
                    }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
            }
        }
        etDuration.setOnClickListener { showCustomDialog() }
        tvLength.setOnClickListener { rgLength.visibility = if (rgLength.visibility == View.GONE) View.VISIBLE else View.GONE }
        tvGender.setOnClickListener { rgGender.visibility = if (rgGender.visibility == View.GONE) View.VISIBLE else View.GONE }
        val url = getString(R.string.url,"get_style_filters.php")
        val stringRequest = object : StringRequest(
            Method.GET, url, Response.Listener { response ->
                val obj = JSONObject(response)
                var length = obj.getInt("length")
                var gender = obj.getInt("gender")
                if (length == 0) {length = rgLength.childCount-1} else if (length == rgLength.childCount-1){length = 0}
                if (gender == 0) {gender = rgGender.childCount-1} else if (gender ==  rgGender.childCount-1){gender = 0}
                (rgLength.getChildAt(length) as RadioButton).isChecked = true
                (rgGender.getChildAt(gender) as RadioButton).isChecked = true},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["style_id"] = styleItem.id
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        return rootView
    }

    private fun showCustomDialog() {
        val dialog = Dialog(requireContext())
        val minOptions = mutableListOf<String>()
        for (i in 15 until 315 step(15)){minOptions.add(i.toString()) }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.number_picker_layout)
        val numPicker = dialog.findViewById<NumberPicker>(R.id.numPicker)
        val save = dialog.findViewById<TextView>(R.id.save)
        val close = dialog.findViewById<TextView>(R.id.close)

        numPicker.minValue = 0
        numPicker.maxValue = 19
        numPicker.displayedValues = minOptions.toTypedArray()
        numPicker.setOnValueChangedListener { numberPicker, _, _ -> val x = minOptions[numberPicker.value]; minute = x.toInt()}
        close.setOnClickListener { dialog.dismiss() }
        save.setOnClickListener {dialog.dismiss(); etDuration.setText(getString(R.string.time_mins,minute.toString())) }
        dialog.show()

}}