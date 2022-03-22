package com.example.saloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class SaloonImageAdapter(private var images: MutableList<String>, val clickListener: (Int) -> Unit)
    : RecyclerView.Adapter<SaloonImageAdapter.SaloonImageViewHolder>(){


    inner class SaloonImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val image = itemView.findViewById<ImageView>(R.id.image)
        fun bind(index: Int){
            if (index == 0){
                image.setImageDrawable(AppCompatResources.getDrawable(itemView.context,R.drawable.add))
            }else{
                Picasso.get().load(itemView.context.getString(
                R.string.url,"saloon_images/${images[index]}.jpeg")).fit().centerCrop().into(image)
            }
            image.setOnClickListener { clickListener(index) }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaloonImageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.image_layout,
            parent, false)
        return SaloonImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SaloonImageViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount() = images.size
}