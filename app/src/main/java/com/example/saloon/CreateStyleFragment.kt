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
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.textfield.TextInputEditText
import java.util.ArrayList

class CreateStyleFragment : Fragment() {

    private lateinit var etDuration: AutoCompleteTextView
    private var minute = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_create_style, container, false)
        val etName = rootView.findViewById<TextInputEditText>(R.id.etName)
        val etPrice = rootView.findViewById<TextInputEditText>(R.id.etPrice)
        val tvAddImage = rootView.findViewById<TextView>(R.id.tvAddImage)
        val ivStyleImage = rootView.findViewById<ImageSlider>(R.id.ivStyleImage)
        val btnCreateStyle = rootView.findViewById<Button>(R.id.btnCreateStyle)
        val etInfo = rootView.findViewById<TextInputEditText>(R.id.etInfo)
        etDuration = rootView.findViewById(R.id.etDuration)
        val tvGender = rootView.findViewById<TextView>(R.id.tvGender)
        val tvLength = rootView.findViewById<TextView>(R.id.tvLength)
        val rgGender = rootView.findViewById<RadioGroup>(R.id.rgGender)
        val rgLength = rootView.findViewById<RadioGroup>(R.id.rgLength)

        etDuration.setOnClickListener { showCustomDialog() }

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.ic_baseline_add_circle_24))
        ivStyleImage.setImageList(imageList)
        ivStyleImage.setItemClickListener(object:ItemClickListener{override fun onItemSelected(position: Int) {addImage(position)}})
        btnCreateStyle.setOnClickListener { view ->
            var filled = true
            if (etName.text!!.isEmpty()){filled=false;etName.error="This field must be filled"}
            if (etPrice.text!!.isEmpty()){filled=false;etPrice.error="This field must be filled"}
            if (minute == 0){filled=false;etDuration.error="This field must be filled"}
            if (filled){
                val url = getString(R.string.url,"style_check.php")
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        if (response == "1"){
                            Toast.makeText(context, "Style Already Exists",Toast.LENGTH_SHORT).show() }
                        else{
                            val genderId: Int = rgGender.checkedRadioButtonId
                            val genderButton: View = rgGender.findViewById(genderId)
                            var gender = rgGender.indexOfChild(genderButton)
                            val lengthId: Int = rgLength.checkedRadioButtonId
                            val lengthButton: View = rgLength.findViewById(lengthId)
                            var length = rgLength.indexOfChild(lengthButton)
                            if (length == 0) {length = rgLength.childCount-1} else if (length == rgLength.childCount-1){length = 0}
                            if (gender == 0) {gender = rgGender.childCount-1} else if (gender ==  rgGender.childCount-1){gender = 0}
                            val filterItem = StyleFilterItem(gender,length)
                            val timeItem = TimeItem(minute.toString())
                            val styleItem = StyleItem(etName.text.toString(),etPrice.text.toString().toFloat(),timeItem,
                                etInfo.text.toString(),filterItem=filterItem)
                            val bundle = bundleOf(Pair("styleItem",styleItem))
                            view.findNavController().navigate(R.id.action_createStyleFragment_to_chooseCategoryFragment,bundle)
                        } },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["name"] = etName.text.toString()
                        return params
                    }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest) }
        }

        tvLength.setOnClickListener { rgLength.visibility = if (rgLength.visibility == View.GONE) View.VISIBLE else View.GONE }
        tvGender.setOnClickListener { rgGender.visibility = if (rgGender.visibility == View.GONE) View.VISIBLE else View.GONE }
        return rootView
    }
    private fun addImage(index: Int) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.add_image_layout)
        val remove = dialog.findViewById<TextView>(R.id.remove)
        val add = dialog.findViewById<TextView>(R.id.add)
        if (index == 0){remove.visibility = View.GONE}
        remove.setOnClickListener { dialog.dismiss() }
        add.setOnClickListener {dialog.dismiss(); }
        dialog.show() }
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
        numPicker.setOnValueChangedListener { numberPicker, _, _ ->
            val x = minOptions[numberPicker.value]; minute = x.toInt()}
        close.setOnClickListener { dialog.dismiss() }
        save.setOnClickListener {dialog.dismiss(); etDuration.setText(getString(R.string.time_mins,minute.toString())) }
        dialog.show() }
}