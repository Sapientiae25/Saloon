package com.example.saloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import java.util.*


class StyleCategoryAdapter (private val categories: MutableList<CategoryItem>,val activity: DefaultActivity,
                            val accountItem: AccountItem)
    : RecyclerView.Adapter<StyleCategoryAdapter.StyleCategoryViewHolder>(){

    private lateinit var communicator : ChangeFragment

    inner class StyleCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val cvCategory: CardView = itemView.findViewById(R.id.cvCategory)
        private val ivCategory: ImageView = itemView.findViewById(R.id.ivCategory)

        fun bind(index: Int){
            if (index == 0){
                tvCategory.text = itemView.context.getString(R.string.add_category)
                ivCategory.setImageResource(R.drawable.ic_baseline_add_circle_24)
                cvCategory.setOnClickListener {
                    communicator = activity
                    communicator.change(CreateCategory.newInstance(accountItem)) }
            }else{
                val currentItem = categories[index]
                tvCategory.text = currentItem.category
                cvCategory.setOnClickListener {
                    communicator = activity
                    communicator.change(CategoryFragment.newInstance(accountItem,currentItem)) } }
        } }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StyleCategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.style_category_layout,
            parent, false)
        return StyleCategoryViewHolder(itemView) }
    override fun onBindViewHolder(holder: StyleCategoryViewHolder, position: Int) {
        holder.bind(position) }
    override fun getItemCount() = categories.size
}