package com.example.saloon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONObject
import java.util.*

class CalendarBottomSheetFragment : BottomSheetDialogFragment(){

    lateinit var tvEnd: TextView
    lateinit var tvStart: TextView
    private lateinit var communicator: UpdateCalendar
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.calendar_bottom_sheet_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        communicator = activity as CalendarActivity
        val startTime = arguments?.getString("startTime")
        val accountItem = arguments?.getParcelable<AccountItem>("accountItem")
        tvEnd = view.findViewById(R.id.tvEnd)
        tvStart = view.findViewById(R.id.tvStart)
        tvStart.text = startTime
        val llEndTime = view.findViewById<LinearLayout>(R.id.llEndTime)
        val llStartTime = view.findViewById<LinearLayout>(R.id.llStartTime)
        val btnAddBreak = view.findViewById<AppCompatButton>(R.id.btnAddBreak)
        val timeList = mutableListOf<String>()
            for (h in 0 until 24){
                for (m in 0 until 60 step 15){
                    timeList.add(getString(R.string.clock,h,m)) }}

        llEndTime.setOnClickListener {
            val popupMenu = PopupMenu(context,tvEnd)
            popupMenu.inflate(R.menu.time_menu)
            val startSplit = tvStart.text.toString().split(":")
            val startHour = startSplit[0].toInt()
            val startMinute = startSplit[1].toInt()
            for (i in 0 until timeList.size){
                val time = timeList[i]
                val endSplit = time.split(":")
                val endHour = endSplit[0].toInt()
                val endMinute = endSplit[1].toInt()
                var larger = false
                if (endHour > startHour ){larger = true}
                else if (endHour == startHour && endMinute > startMinute){larger = true}
                if (larger){ popupMenu.menu.add(0,i,i,time)}}
            popupMenu.setOnMenuItemClickListener { item ->
                tvEnd.text = timeList[item.itemId]
                true }
            popupMenu.show()
            }

        llStartTime.setOnClickListener {
            val popupMenu = PopupMenu(context,tvStart)
            popupMenu.inflate(R.menu.time_menu)
            for (i in 0 until timeList.size){
                val time = timeList[i]
                popupMenu.menu.add(0,i,i,time)}
            popupMenu.setOnMenuItemClickListener { item ->
                tvStart.text = timeList[item.itemId]
                true
            }
            popupMenu.show() }
        btnAddBreak.setOnClickListener {
            if (tvEnd.text == "00:00"){Toast.makeText(context,"Please add valid time",Toast.LENGTH_SHORT).show()}
            else{
                val url = "http://192.168.1.102:8012/saloon/break.php"
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response -> println(response)
                        val obj = JSONObject(response)
                        val firstDay = obj.getInt("first_day") / 5
                        val lastDay = obj.getInt("last_day") / 5
                        communicator.update(firstDay,lastDay)
                    },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["account_id"] = accountItem!!.id
                        params["break_start"] = tvStart.text.toString()
                        params["break_end"] = tvEnd.text.toString()
                        return params
                    }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
                dismiss()}
        }

    }
}