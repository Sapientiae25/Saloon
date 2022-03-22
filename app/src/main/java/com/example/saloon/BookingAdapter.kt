package com.example.saloon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.squareup.picasso.Picasso
import java.util.HashMap


class BookingAdapter (private val bookingList: MutableList<BookingItem>)
    : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>(){

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val tvName = itemView.findViewById<TextView>(R.id.tvName)
        private val tvCost = itemView.findViewById<TextView>(R.id.tvCost)
        private val tvStyleDuration = itemView.findViewById<TextView>(R.id.tvStyleDuration)
        private val tvTimePeriod = itemView.findViewById<TextView>(R.id.tvTimePeriod)
        private val tvEmail = itemView.findViewById<TextView>(R.id.tvEmail)
        private val image = itemView.findViewById<ImageView>(R.id.image)

        fun bind(index: Int){
            val currentItem = bookingList[index]
            tvName.text = currentItem.name
            tvCost.text = currentItem.cost
            tvStyleDuration.text = currentItem.duration
            tvTimePeriod.text = itemView.context.getString(R.string.time_distance,currentItem.start,currentItem.end)
            tvEmail.text = currentItem.name
            tvName.text = currentItem.name
            itemView.setOnClickListener {
                val bookingBottomSheetFragment = BookingBottomSheetFragment()
                bookingBottomSheetFragment.show((itemView.context as DefaultActivity).supportFragmentManager, "BottomSheetDialog")
                val bundle = Bundle()
                bundle.putParcelable("bookingItem",currentItem)
                bookingBottomSheetFragment.arguments = bundle }
            if (currentItem.imageId.isEmpty()){ image.visibility = View.GONE }else{
                Picasso.get().load(itemView.context.getString(
                    R.string.url,"style_images/${currentItem.imageId}.jpeg")).fit().centerCrop().into(image)
            }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.booking_layout,
            parent, false)
        return BookingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount() = bookingList.size


}