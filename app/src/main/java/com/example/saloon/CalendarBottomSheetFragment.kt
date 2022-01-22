package com.example.saloon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONArray
import java.time.LocalTime
import java.util.*
import kotlin.collections.ArrayList

class CalendarBottomSheetFragment : BottomSheetDialogFragment(){

    var communicator: RestartCalendar? = null
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
        val accountItem = arguments?.getParcelable<AccountItem>("accountItem")
        val date = booking.date
        val tvBreak = view.findViewById<TextView>(R.id.tvBreak)
        if (breakType) tvBreak.text = getString(R.string.edit_break)
        val tvEnd = view.findViewById<TextView>(R.id.tvEnd)
        val tvStart = view.findViewById<TextView>(R.id.tvStart)
        tvStart.text = startTime
        tvEnd.text = endTime
        val llEndTime = view.findViewById<LinearLayout>(R.id.llEndTime)
        val llStartTime = view.findViewById<LinearLayout>(R.id.llStartTime)
        val btnAddBreak = view.findViewById<AppCompatButton>(R.id.btnAddBreak)
        communicator = activity as RestartCalendar
        llEndTime.setOnClickListener { (activity as DefaultActivity).showCustomDialog(tvEnd)}
        llStartTime.setOnClickListener { (activity as DefaultActivity).showCustomDialog(tvStart) }
        btnAddBreak.setOnClickListener {
            val startTimeObj = LocalTime.parse(tvStart.text)
            val endTimeObj = LocalTime.parse(tvEnd.text)
            if (startTimeObj < endTimeObj){
                val startDatetime = getString(R.string.make_datetime,date,tvStart.text)
                val endDatetime = getString(R.string.make_datetime,date,tvEnd.text)
                val bookingArray = mutableListOf<CalendarItem>()
                val url = "http://192.168.1.102:8012/saloon/break_check.php"
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response -> println(response)
                        val arr = JSONArray(response)
                        for (y in 0 until arr.length()){
                            val obj = arr.getJSONObject(y)
                            val start = obj.getString("start")
                            val end = obj.getString("end")
                            val id = obj.getInt("id")
                            val bookType = obj.getInt("type")
                            val style = obj.getString("style")
                            bookingArray.add(CalendarItem(start,end, name=style,id=id, calendarType=bookType))
                        }
                        if (arr.length() > 0){
                            val dialog = BreakCheckPopUp()
                            val bundle = Bundle()
                            bundle.putString("startDatetime", startDatetime)
                            bundle.putString("endDatetime", endDatetime)
                            bundle.putParcelable("accountItem", accountItem)
                            bundle.putParcelableArrayList("booking", ArrayList(bookingArray))
                            dialog.arguments = bundle
                            dialog.show(parentFragmentManager,"customDialog")
                        }else{
                            if (booking.calendarType == 1){
                                val url2 = "http://192.168.1.102:8012/saloon/edit_break.php"
                                val stringRequest = object : StringRequest(
                                    Method.POST, url2, Response.Listener { response -> println(response)
                                    },
                                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                                    @Throws(AuthFailureError::class)
                                    override fun getParams(): Map<String, String> {
                                        val params = HashMap<String, String>()
                                        params["break_id"] = booking.id.toString()
                                        params["break_start"] = startDatetime
                                        params["break_end"] = endDatetime
                                        return params
                                    }}
                                VolleySingleton.instance?.addToRequestQueue(stringRequest)
                            }else{
                            val url2 = "http://192.168.1.102:8012/saloon/break.php"
                            val stringRequest = object : StringRequest(
                                Method.POST, url2, Response.Listener { response -> println(response)
                                },
                                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                                @Throws(AuthFailureError::class)
                                override fun getParams(): Map<String, String> {
                                    val params = HashMap<String, String>()
                                    params["account_id"] = accountItem!!.id
                                    params["break_start"] = startDatetime
                                    params["break_end"] = endDatetime
                                    return params
                                }}
                            VolleySingleton.instance?.addToRequestQueue(stringRequest) }
                            communicator?.restart()
                            dismiss()} },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["account_id"] = accountItem!!.id
                        params["start_time"] = startDatetime
                        params["end_time"] = endDatetime
                        params["exist_id"] = booking.id.toString()
                        return params
                    }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }}
}


}