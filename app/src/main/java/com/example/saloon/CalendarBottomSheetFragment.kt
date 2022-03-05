package com.example.saloon

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONObject
import java.time.LocalTime

class CalendarBottomSheetFragment(val fragment: CalendarFragment) : BottomSheetDialogFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.calendar_bottom_sheet_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val booking = arguments?.getParcelable<CalendarItem>("booking")!!
        val startTime = booking.start
        val endTime = booking.end
        val breakType = booking.calendarType == 1
        val accountItem = (activity as DefaultActivity).accountItem
        val tvBreak = view.findViewById<TextView>(R.id.tvBreak)
        if (breakType) tvBreak.text = getString(R.string.edit_break)
        val tvEnd = view.findViewById<TextView>(R.id.tvEnd)
        val tvStart = view.findViewById<TextView>(R.id.tvStart)
        tvStart.text = startTime
        tvEnd.text = endTime
        val llEndTime = view.findViewById<LinearLayout>(R.id.llEndTime)
        val llStartTime = view.findViewById<LinearLayout>(R.id.llStartTime)
        val btnAddBreak = view.findViewById<AppCompatButton>(R.id.btnAddBreak)
        val btnDelete = view.findViewById<AppCompatButton>(R.id.btnDelete)
        llEndTime.setOnClickListener { showCustomDialog(tvEnd)}
        llStartTime.setOnClickListener { showCustomDialog(tvStart) }
        btnAddBreak.setOnClickListener {
            val startTimeObj = LocalTime.parse(tvStart.text)
            val endTimeObj = LocalTime.parse(tvEnd.text)
            if (startTimeObj < endTimeObj){
                val startDatetime = getString(R.string.make_datetime,booking.date,tvStart.text)
                val endDatetime = getString(R.string.make_datetime,booking.date,tvEnd.text)
                val bookingArray = mutableListOf<CalendarItem>()
                val url = getString(R.string.url,"break_check.php")
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        val jObj = JSONObject(response)
                        val past = jObj.getInt("past") == 1
                        if (!past){
                            val arr = jObj.getJSONArray("breaks")
                            for (y in 0 until arr.length()){
                                val obj = arr.getJSONObject(y)
                                val start = obj.getString("start")
                                val end = obj.getString("end")
                                val id = obj.getInt("id")
                                val bookType = obj.getInt("type")
                                val style = obj.getString("style")
                                bookingArray.add(CalendarItem(start,end, name=style,id=id, calendarType=bookType)) }
                            if (arr.length() != 0){
                                val dialog = BreakCheckPopUp(fragment)
                                val bundle = Bundle()
                                bundle.putString("startDatetime", startDatetime)
                                bundle.putString("endDatetime", endDatetime)
                                bundle.putParcelable("accountItem", accountItem)
                                bundle.putParcelableArrayList("booking", ArrayList(bookingArray))
                                dialog.arguments = bundle
                                dialog.show(parentFragmentManager,"customDialog")
                            }else{
                                if (booking.calendarType == 1){
                                    val url2 = getString(R.string.url,"edit_break.php")
                                    val stringRequest = object : StringRequest(
                                        Method.POST, url2, Response.Listener {fragment.restart(); dismiss() },
                                        Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                                        @Throws(AuthFailureError::class)
                                        override fun getParams(): Map<String, String> {
                                            val params = HashMap<String, String>()
                                            params["break_id"] = booking.id.toString()
                                            params["break_start"] = startDatetime
                                            params["break_end"] = endDatetime
                                            params["account_id"] = accountItem.id
                                            return params
                                        }}
                                    VolleySingleton.instance?.addToRequestQueue(stringRequest)
                                }else{
                                    val url2 = getString(R.string.url,"break.php")
                                    val stringRequest = object : StringRequest(
                                        Method.POST, url2, Response.Listener { fragment.restart(); dismiss() },
                                        Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                                        @Throws(AuthFailureError::class)
                                        override fun getParams(): Map<String, String> {
                                            val params = HashMap<String, String>()
                                            params["account_id"] = accountItem.id
                                            params["break_start"] = startDatetime
                                            params["break_end"] = endDatetime
                                            return params }}
                                    VolleySingleton.instance?.addToRequestQueue(stringRequest) }
                        }} else{Toast.makeText(context,"Date Has Passed",Toast.LENGTH_SHORT).show()} },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["account_id"] = accountItem.id
                        params["start_time"] = startDatetime
                        params["end_time"] = endDatetime
                        params["exist_id"] = booking.id.toString()
                        return params
                    }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest) }}
        btnDelete.setOnClickListener{
            val url = getString(R.string.url,"delete_break.php")
            val stringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { response -> println(response) },
                Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["break_id"] = booking.id.toString()
                    return params }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest) }
}

    private fun showCustomDialog(textView: TextView) {
        val dialog = Dialog(requireContext())
        var hour = 0
        var minute = 0
        val minOptions = arrayOf("00","15","30","45")
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.time_picker_layout)
        val numPickerHour = dialog.findViewById<NumberPicker>(R.id.numPickerHour)
        val numPickerMins = dialog.findViewById<NumberPicker>(R.id.numPickerMins)
        numPickerHour.value = textView.text.toString().split(":")[0].toInt()
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
        save.setOnClickListener {textView.text = getString(R.string.clock,hour,minute);  dialog.dismiss() }
        dialog.show()
    }

}