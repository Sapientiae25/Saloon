package com.example.saloon

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView

class CreateStyleImageAdapter(private var images: MutableList<Bitmap>, val clickListener: (Int) -> Unit)
    : RecyclerView.Adapter<CreateStyleImageAdapter.CreateStyleImageViewHolder>(){


    inner class CreateStyleImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val image = itemView.findViewById<ImageView>(R.id.image)
        fun bind(index: Int){
            if (index == 0){
                image.setImageDrawable(AppCompatResources.getDrawable(itemView.context,R.drawable.ic_baseline_add_circle_24))
            }else{
                image.setImageBitmap(images[0])
            }
            image.setOnClickListener {
                clickListener(index)
            }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateStyleImageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.image_layout,
            parent, false)
        return CreateStyleImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CreateStyleImageViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount() = images.size
}