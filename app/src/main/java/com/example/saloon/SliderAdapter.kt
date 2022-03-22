package com.example.saloon

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter

class SliderAdapter(var context: Context, private var images: Array<Int>)
    : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>(){

    inner class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val text = itemView.findViewById<TextView>(R.id.text)
        fun bind(index: Int){text.text = index.toString()}

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.test_layout,
            parent, false)
        return SliderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount() = images.size

}