package com.example.saloon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray


class CalendarAdapter (private val calendarArray: JSONArray,val accountItem: AccountItem)
    : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>(){

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(index: Int){
            val currentItem = calendarArray.getJSONObject(index)
            val tvCalendar = itemView.findViewById<TextView>(R.id.tvCalendar)
            val value = currentItem.get("value")
            val pos = currentItem.getInt("position")
            if (pos == 0){tvCalendar.setBackgroundResource(R.drawable.top_border)}
            else if (pos == 4){tvCalendar.setBackgroundResource(R.drawable.bottom_border)
                tvCalendar.text = currentItem.getString("startTime")}
            if (value == "1"){
                if (pos == 0){val name = currentItem.getString("name"); tvCalendar.text = name}
                tvCalendar.setBackgroundResource(R.color.red)
                tvCalendar.setTextColor(itemView.context.getColor(R.color.teal_200))
            }else if (value == "2"){
                tvCalendar.setBackgroundResource(R.color.light_grey)

                tvCalendar.setTextColor(itemView.context.getColor(R.color.red))
            }
            tvCalendar.setOnClickListener {
                val bottomSheetFragment = CalendarBottomSheetFragment()
                bottomSheetFragment.show((itemView.context as CalendarActivity).supportFragmentManager,
                    "BottomSheetDialog")
                val bundle = Bundle()
                bundle.putString("startTime",currentItem.getString("startTime"))
                bundle.putParcelable("accountItem",accountItem)
                bottomSheetFragment.arguments = bundle
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.calendar_cell_layout,
            parent, false)
        val width = parent.measuredWidth / 5
        itemView.layoutParams.width = width
        return CalendarViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount() = calendarArray.length()


}