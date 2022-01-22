package com.example.saloon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import java.util.*

class BreakCheckPopUp : DialogFragment(), DeleteEvent {
    private lateinit var editBtn: Button
    private lateinit var endDatetime: String
    private lateinit var startDatetime: String
    private lateinit var accountItem: AccountItem
    var communicator: RestartCalendar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.activity_break_check_pop_up,null)
        val bookingArray = arguments?.getParcelableArrayList<CalendarItem>("booking")
        endDatetime = arguments?.getString("endDatetime")!!
        startDatetime = arguments?.getString("startDatetime")!!
        accountItem = arguments?.getParcelable("accountItem")!!

        val rvEvents = rootView.findViewById<RecyclerView>(R.id.rvEvents)
        editBtn = rootView.findViewById(R.id.editBtn)
        val cancelBtn = rootView.findViewById<Button>(R.id.cancelBtn)
        rvEvents.layoutManager = LinearLayoutManager(context)
        val adapter = EventCheckAdapter(bookingArray!!.toMutableList())
        adapter.communicator = this
        rvEvents.adapter = adapter
        rvEvents.adapter?.notifyItemRangeInserted(0,bookingArray.size)


        editBtn.setOnClickListener {
            dismiss()
        }
        cancelBtn.setOnClickListener {
            dismiss()
        }
        return rootView
    }

    override fun deletes() {
        editBtn.text = getString(R.string.save)
        editBtn.setOnClickListener {
        val url = "http://192.168.1.102:8012/saloon/break.php"
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response -> println(response)
            },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["account_id"] = accountItem.id
                params["break_start"] = startDatetime
                params["break_end"] = endDatetime
                return params }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest)
            communicator = activity as RestartCalendar
            communicator?.restart()
            dismiss()
        }

    }
}