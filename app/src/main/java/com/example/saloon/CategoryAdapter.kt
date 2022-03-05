package com.example.saloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter (private val styleItemList: MutableList<StyleItem>)
    : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val name: TextView = itemView.findViewById(R.id.name)
        private val price: TextView = itemView.findViewById(R.id.price)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private val rating: RatingBar = itemView.findViewById(R.id.rating)
        private val privacy: TextView = itemView.findViewById(R.id.privacy)

        fun bind(index: Int){
            val currentItem = styleItemList[index]
            val timeItem = currentItem.time
            name.text = currentItem.name
            price.text = itemView.context.getString(R.string.money,currentItem.price)
            tvAddress.text = currentItem.accountItem.addressItem?.address
            if (currentItem.rating == null) {rating.visibility = View.GONE} else {rating.rating = currentItem.rating.toFloat()}
            val timeValue = if (timeItem.maxTime.isNullOrEmpty()) timeItem.time
            else itemView.context.getString(R.string.time_distance,timeItem.time,timeItem.maxTime)
            if (currentItem.privacy) {privacy.visibility = View.VISIBLE}
            time.text = itemView.context.getString(R.string.time_mins,timeValue)
            itemView.setOnClickListener { view ->
                val bundle = bundleOf(Pair("styleItem",currentItem))
                view.findNavController().navigate(R.id.action_categoryFragment_to_styleFragment,bundle) }
        } }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.style_layout,
            parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount() = styleItemList.size
}