package com.example.saloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class StyleImageAdapter(private var images: MutableList<String>,val clickListener: (Int) -> Unit)
    : RecyclerView.Adapter<StyleImageAdapter.StyleImageViewHolder>(){

    inner class StyleImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val image = itemView.findViewById<ImageView>(R.id.image)
        fun bind(index: Int){
            if (index == 0){
                image.setImageDrawable(AppCompatResources.getDrawable(itemView.context,R.drawable.ic_baseline_add_circle_24))
            }else{
                Picasso.get().load(itemView.context.getString(
                    R.string.url,"style_images/${images[index]}.jpeg")).fit().centerCrop().into(image) }
            image.setOnClickListener { clickListener(index) }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StyleImageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.image_layout, parent, false)
        return StyleImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StyleImageViewHolder, position: Int) { holder.bind(position) }
    override fun getItemCount() = images.size
}