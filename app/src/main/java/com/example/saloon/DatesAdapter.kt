package com.example.saloon
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.android.volley.AuthFailureError
//import com.android.volley.Response
//import com.android.volley.toolbox.StringRequest
//import org.json.JSONObject
//import java.util.*
//
//class DatesAdapter (private val dateList:MutableList<MutableList<String>>,val accountItem: AccountItem, val activity: CalendarActivity,
//                    val calendar: MutableList<CalendarItem>)
//    : RecyclerView.Adapter<DatesAdapter.DatesViewHolder>()  {
//
//    inner class DatesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
////        private val tvDate1: TextView = itemView.findViewById(R.id.tvDate1)
////        private val tvDate2: TextView = itemView.findViewById(R.id.tvDate2)
////        private val tvDate3: TextView = itemView.findViewById(R.id.tvDate3)
////        private val tvDate4: TextView = itemView.findViewById(R.id.tvDate4)
////        private val tvDate5: TextView = itemView.findViewById(R.id.tvDate5)
////        val rvCalendar: RecyclerView = activity.findViewById(R.id.rvCalendar)
//
//        fun bind(index: Int){
//            setDates(index)
//            makeCalendar(index)
////            tvDate.setOnClickListener { makeCalendar(index); chosenDay = index }
//        }
//        private fun setDates(index: Int){
//            val currentItem = dateList[index]
////            tvDate1.text = currentItem[0].split("-")[2].toInt().toString()
////            tvDate2.text = currentItem[1].split("-")[2].toInt().toString()
////            tvDate3.text = currentItem[2].split("-")[2].toInt().toString()
////            tvDate4.text = currentItem[3].split("-")[2].toInt().toString()
////            tvDate5.text = currentItem[4].split("-")[2].toInt().toString()
//        }
//    private fun makeCalendar(index: Int){
//        val currentItem = dateList[index]
////        rvCalendar.adapter = CalendarAdapter(calendar,accountItem)
////        rvCalendar.layoutManager = LinearLayoutManager(itemView.context)
////        rvCalendar.hasFixedSize()
////        rvCalendar.adapter?.notifyItemRangeInserted(0,calendar.size)
//        val stringRequest = object : StringRequest(
//            Method.POST, url, Response.Listener { response ->
//                println(response)
//                val obj = JSONObject(response)
//                val dates = obj.getJSONArray("dates")
//                val breaksArray = obj.getJSONArray("break")
//                for (x in 0 until dates.length()) {
//                    val date = dates.getJSONObject(x)
//                    val timePosition = date.getInt("position")
//                    val duration = date.getInt("duration")
//                    val styleName = date.getString("name")
//                    val startBook = date.getString("start")
//                    val endBook = date.getString("end")
//                    for (i in 0 until duration){
//                        val interval = JSONObject()
//                        interval.put("name",styleName);interval.put("value", "1")
//                        interval.put("position",i)
//                        interval.put("startTime", startBook)
//                        interval.put("endTime", endBook)
//                        rvCalendar.adapter?.notifyItemChanged(timePosition)
//                    } }
//                for (x in 0 until breaksArray.length()) {
//                    val breaks = breaksArray.getJSONObject(x)
//                    val timePosition = breaks.getInt("position")
//                    val rowCount = breaks.getInt("row_count")
//                    val startBreak = breaks.getString("start")
//                    val endBreak = breaks.getString("end")
//                    val removeHours = breaks.getInt("remove_hours")
//                    val firstSpan = breaks.getInt("first_span")
//                    val finalSpan = breaks.getInt("final_span")
//                    calendar[timePosition-1].span = firstSpan
//                    for (y in 1 until removeHours+1){
//                        calendar[timePosition+y].gone = true
//                        rvCalendar.adapter?.notifyItemChanged(timePosition+y)
//                        if (y == removeHours){
//                            calendar[timePosition+y+1].span = finalSpan
//                            rvCalendar.adapter?.notifyItemRangeChanged(timePosition,timePosition+y+1)}
//                    }
//                    val item = CalendarItem(startBreak,endBreak,"",rowCount,1)
//                    calendar[timePosition] = item
//                } },
//            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
//            @Throws(AuthFailureError::class)
//            override fun getParams(): Map<String, String> {
//                val params = HashMap<String, String>()
//                params["account_id"] = accountItem.id
//                params["first_day"] = calendar[0].date
//                return params
//            }}
//        VolleySingleton.instance?.addToRequestQueue(stringRequest)
//    }
//
//    }
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatesViewHolder {
//        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.date_layout,
//            parent, false)
//        return DatesViewHolder(itemView)
//    }
//
//    override fun onBindViewHolder(holder: DatesViewHolder, position: Int) {
//        holder.bind(position)
//    }
//    override fun getItemCount() = dateList.size
//}