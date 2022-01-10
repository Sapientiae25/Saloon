package com.example.saloon

import android.content.Intent
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.saloon.StyleItem

class StyleItemAdapter (private val styleItemList: MutableList<StyleItem>,private val accountItem: AccountItem)
    : RecyclerView.Adapter<StyleItemAdapter.StyleItemViewHolder>() {

    inner class StyleItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val name: TextView = itemView.findViewById(R.id.name)
        private val price: TextView = itemView.findViewById(R.id.price)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val styleLayout: LinearLayout = itemView.findViewById(R.id.styleLayout)

        fun bind(index: Int){
            val currentItem = styleItemList[index]
            val timeItem = currentItem.time
            name.text = currentItem.name
            price.text = itemView.context.getString(R.string.money,currentItem.price)
            val timeValue = if (timeItem.maxTime != null) itemView.context.getString(R.string.time_distance,timeItem.time,timeItem.maxTime)
            else timeItem.time
            time.text = itemView.context.getString(R.string.time_mins,timeValue)
            styleLayout.setOnClickListener {
                val intent = Intent(itemView.context, StyleActivity::class.java)
                intent.putExtra("account_item", accountItem)
                intent.putExtra("style_item", currentItem)
                itemView.context.startActivity(intent)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StyleItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.style_layout,
            parent, false)
        return StyleItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StyleItemViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount() = styleItemList.size
}