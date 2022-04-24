package com.example.saloon

import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class SimilarAdapter (private val styleItemList: MutableList<StyleItem>)
    : RecyclerView.Adapter<SimilarAdapter.SimilarViewHolder>() {

    inner class SimilarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val name: TextView = itemView.findViewById(R.id.name)
        private val image = itemView.findViewById<ImageView>(R.id.image)

        fun bind(index: Int){
            val currentItem = styleItemList[index]
            name.text = currentItem.name
            if (currentItem.imageId.isNotEmpty())
//            { image.visibility = View.GONE }else
            {
                Picasso.get().load(itemView.context.getString(
                    R.string.url,"style_images/${currentItem.imageId}.jpeg")).fit().centerCrop().into(image)
        }
            itemView.setOnClickListener { view ->
                val bundle = bundleOf(Pair("styleItem",currentItem))
                view.findNavController().navigate(R.id.action_styleFragment_self,bundle) } } }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.similar_layout,
            parent, false)
        return SimilarViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SimilarViewHolder, position: Int) {
        holder.bind(position)

    }
    override fun getItemCount() = styleItemList.size
}