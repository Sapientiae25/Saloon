package com.example.saloon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class TestAdapter (private val calendarArray: MutableList<CalendarItem>)
    : RecyclerView.Adapter<TestAdapter.TestViewHolder>(){


    inner class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvCalendar: TextView = itemView.findViewById(R.id.tvCalendar)
        fun bind(index: Int){
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.calendar_cell_layout,
            parent, false)
        return TestViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount() = calendarArray.size


}