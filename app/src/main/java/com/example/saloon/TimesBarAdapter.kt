package com.example.saloon

import android.content.ClipDescription
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TimesBarAdapter (val timeList: MutableList<String>)
    : RecyclerView.Adapter<TimesBarAdapter.TimesBarViewHolder>() {

    inner class TimesBarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(index: Int){
            val currentItem = timeList[index]
            tvTime.text = currentItem
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimesBarViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.time_bar_layout,
            parent, false)
        return TimesBarViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TimesBarViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount() = timeList.size
}