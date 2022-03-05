package com.example.saloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView


class CategoryChoiceAdapter (private val checkedList: MutableList<CheckItem>)
    : RecyclerView.Adapter<CategoryChoiceAdapter.CategoryChoiceViewHolder>(){

    inner class CategoryChoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val rbChecked: RadioButton = itemView.findViewById(R.id.rbChecked)

        fun bind(index: Int){
            val currentItem = checkedList[index]
            rbChecked.text = currentItem.style
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