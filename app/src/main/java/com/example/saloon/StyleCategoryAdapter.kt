package com.example.saloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

class StyleCategoryAdapter (private val categories: MutableList<CategoryItem>)
    : RecyclerView.Adapter<StyleCategoryAdapter.StyleCategoryViewHolder>(){

    inner class StyleCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val ivCategory: ImageView = itemView.findViewById(R.id.ivCategory)

        fun bind(index: Int){val currentItem = categories[index]
            if (index == 0){
                ivCategory.setImageDrawable(AppCompatResources.getDrawable(itemView.context,R.drawable.ic_baseline_add_circle_24))
                tvCategory.text = itemView.context.getString(R.string.create_category)
                itemView.setOnClickListener { view -> view.findNavController().navigate(R.id.action_saloonFragment_to_createCategory) }
            }else{
                tvCategory.text = currentItem.category
                itemView.setOnClickListener { view ->
                    val bundle = bundleOf(Pair("categoryItem",currentItem))
                    view.findNavController().navigate(R.id.action_saloonFragment_to_categoryFragment,bundle) }} }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StyleCategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.style_category_layout,
            parent, false)
        return StyleCategoryViewHolder(itemView) }
    override fun onBindViewHolder(holder: StyleCategoryViewHolder, position: Int) {
        holder.bind(position) }
    override fun getItemCount() = categories.size}