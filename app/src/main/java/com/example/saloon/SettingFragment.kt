package com.example.saloon

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import org.json.JSONObject

class SettingFragment : Fragment(){

    private lateinit var tvAddress: TextView
    private lateinit var tvDetails: TextView
    private lateinit var tvOpen: TextView
    private lateinit var tvClose: TextView
    private lateinit var tvPayment: TextView
    private lateinit var tvChangeImage: TextView
    private lateinit var tvPassword: EditText
    private lateinit var accountItem: AccountItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_setting, container, false)
        (activity as DefaultActivity).supportActionBar?.title = "Settings"
        accountItem = (activity as DefaultActivity).accountItem
        tvDetails = rootView.findViewById(R.id.tvDetails)
        tvAddress = rootView.findViewById(R.id.tvAddress)
        tvOpen = rootView.findViewById(R.id.tvOpen)
        tvClose = rootView.findViewById(R.id.tvClose)
        tvPayment = rootView.findViewById(R.id.tvPayment)
        tvPassword = rootView.findViewById(R.id.tvPassword)
        tvChangeImage = rootView.findViewById(R.id.tvChangeImage)
        tvAddress.setOnClickListener { view -> view.findNavController().navigate(R.id.action_settingFragment_to_locationFragment)}
        tvDetails.setOnClickListener { view -> view.findNavController().navigate(R.id.action_settingFragment_to_saloonDetailsFragment)}
        tvOpen.setOnClickListener {showCustomDialog(tvOpen,true) }
        tvClose.setOnClickListener {showCustomDialog(tvClose,false) }
        tvPassword.setOnClickListener {view -> view.findNavController().navigate(R.id.action_settingFragment_to_passwordFragment)}
        tvPayment.setOnClickListener { view -> view.findNavController().navigate(R.id.action_settingFragment_to_paymentMethodFragment) }
        val ivStyleImage = rootView.findViewById<ImageSlider>(R.id.ivStyleImage)
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.ic_baseline_add_circle_24))
        ivStyleImage.setImageList(imageList)
        ivStyleImage.setItemClickListener(object: ItemClickListener {override fun onItemSelected(position: Int) {addImage(position)}})
        val url = getString(R.string.url,"open_times.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                println(response)
                val obj = JSONObject(response)
                val open = obj.getString("open")
                val close = obj.getString("close")
                tvOpen.text = getString(R.string.open_,open)
                tvClose.text = getString(R.string.close_,close) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["account_id"] = accountItem.id
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        return rootView
    }
    private fun showCustomDialog(textView: TextView,open: Boolean) {
        val dialog = Dialog(requireContext())
        var hour = 0
        var minute = 0
        val minOptions = arrayOf("00","15","30","45")
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.time_picker_layout)
        val numPickerHour = dialog.findViewById<NumberPicker>(R.id.numPickerHour)
        val numPickerMins = dialog.findViewById<NumberPicker>(R.id.numPickerMins)
        val save = dialog.findViewById<TextView>(R.id.save)
        val close = dialog.findViewById<TextView>(R.id.close)

        numPickerHour.minValue = 0
        numPickerHour.maxValue  = 23
        numPickerMins.minValue = 0
        numPickerMins.maxValue = 3
        numPickerMins.displayedValues = minOptions
        numPickerHour.setOnValueChangedListener { numberPicker, _, _ ->  hour = numberPicker.value}
        numPickerMins.setOnValueChangedListener { numberPicker, _, _ ->
            val x = minOptions[numberPicker.value]
            minute = x.toInt() }
        close.setOnClickListener { dialog.dismiss() }
        save.setOnClickListener {val timeText = getString(R.string.clock,hour,minute);textView.text = timeText
            if (open){
                val url = getString(R.string.url,"open.php")
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener {},
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["account_id"] = accountItem.id
                        params["time"] = timeText
                        return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
                tvOpen.text = getString(R.string.open_,timeText)
            }else{
                val url = getString(R.string.url,"close.php")
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener {},
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["account_id"] = accountItem.id
                        params["time"] = timeText
                        return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
                tvClose.text = getString(R.string.close_,timeText) };dialog.dismiss()
        }
        dialog.show() }
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
}