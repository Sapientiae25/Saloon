package com.example.saloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import java.util.*


class CategoryChoiceAdapter (private val checkedList: MutableList<CheckItem>)
    : RecyclerView.Adapter<CategoryChoiceAdapter.CategoryChoiceViewHolder>(){

    var communicator: DeleteEvent? = null

    inner class CategoryChoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val tvStyle: TextView = itemView.findViewById(R.id.tvStyle)
        private val rbChecked: RadioButton = itemView.findViewById(R.id.rbChecked)

        fun bind(index: Int){
            val currentItem = checkedList[index]
            tvStyle.text = currentItem.style
            itemView.setOnClickListener{
                if (currentItem.checked){
                    rbChecked.isChecked = false
                    currentItem.checked = false
                }else{
                    rbChecked.isChecked = true
                    currentItem.checked = true } } } }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryChoiceViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.check_styles,
            parent, false)
        return CategoryChoiceViewHolder(itemView) }
    override fun onBindViewHolder(holder: CategoryChoiceViewHolder, position: Int) {
        holder.bind(position) }
    override fun getItemCount() = checkedList.size
}