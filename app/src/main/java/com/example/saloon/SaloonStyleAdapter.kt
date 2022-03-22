package com.example.saloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.squareup.picasso.Picasso
import org.json.JSONArray
import java.util.HashMap

class SaloonStyleAdapter (private val styleItemList: MutableList<StyleItem>)
    : RecyclerView.Adapter<SaloonStyleAdapter.SaloonStyleViewHolder>() {

    inner class SaloonStyleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val name: TextView = itemView.findViewById(R.id.name)
        private val price: TextView = itemView.findViewById(R.id.price)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private val rating: RatingBar = itemView.findViewById(R.id.rating)
        private val image: ImageView = itemView.findViewById(R.id.image)

        fun bind(index: Int){
            val currentItem = styleItemList[index]
            val timeItem = currentItem.time
            name.text = currentItem.name
            price.text = itemView.context.getString(R.string.money,currentItem.price)
            tvAddress.text = currentItem.accountItem.addressItem?.address
            if (currentItem.rating == null) {rating.visibility = View.GONE} else {rating.rating = currentItem.rating.toFloat()}
            val timeValue = if (timeItem.maxTime.isNullOrEmpty()) timeItem.time
            else itemView.context.getString(R.string.time_distance,timeItem.time,timeItem.maxTime)
            time.text = itemView.context.getString(R.string.time_mins,timeValue)
            if (currentItem.imageId.isEmpty()){
                image.visibility = View.GONE
            }else{
                Picasso.get().load(itemView.context.getString(
                    R.string.url,"style_images/${currentItem.imageId}.jpeg")).fit().centerCrop().into(image)
            }
            itemView.setOnClickListener { view ->
                val bundle = bundleOf(Pair("styleItem",currentItem))
                view.findNavController().navigate(R.id.action_saloonFragment_to_styleFragment,bundle) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaloonStyleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.style_layout,
            parent, false)
        return SaloonStyleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SaloonStyleViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount() = styleItemList.size
}