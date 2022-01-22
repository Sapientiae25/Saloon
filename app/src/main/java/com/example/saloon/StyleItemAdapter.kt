package com.example.saloon

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StyleItemAdapter (private val styleItemList: MutableList<StyleItem>,private val accountItem: AccountItem,
                        val activity: DefaultActivity)
    : RecyclerView.Adapter<StyleItemAdapter.StyleItemViewHolder>() {
    lateinit var communicator: ChangeFragment

    inner class StyleItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val name: TextView = itemView.findViewById(R.id.name)
        private val price: TextView = itemView.findViewById(R.id.price)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val styleLayout: LinearLayout = itemView.findViewById(R.id.styleLayout)

        fun bind(index: Int){
            val currentItem = styleItemList[index]
            println(currentItem)
            val timeItem = currentItem.time
            name.text = currentItem.name
            price.text = itemView.context.getString(R.string.money,currentItem.price)
            val timeValue = if (timeItem.maxTime != null) itemView.context.getString(R.string.time_distance,timeItem.time,timeItem.maxTime)
            else timeItem.time
            time.text = itemView.context.getString(R.string.time_mins,timeValue)
            communicator = activity
            styleLayout.setOnClickListener {
                communicator.change(StyleFragment.newInstance(accountItem,currentItem))
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