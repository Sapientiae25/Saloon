package com.example.saloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView


class EditCategoryAdapter (private val checkedList: MutableList<CheckItem>,val fragment: EditCategoryFragment)
    : RecyclerView.Adapter<EditCategoryAdapter.EditCategoryAdapterViewHolder>(){

    inner class EditCategoryAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val rbChecked: RadioButton = itemView.findViewById(R.id.rbChecked)
        private var click = 0

        fun bind(index: Int){
            val currentItem = checkedList[index]
            rbChecked.isChecked = currentItem.checked
            rbChecked.text = currentItem.style
            itemView.setOnClickListener{
                if (currentItem.checked){
                    rbChecked.isChecked = false
                    currentItem.checked = false
                    click -= 0
                }else{ click += 0
                    rbChecked.isChecked = true
                    currentItem.checked = true }
                fragment.ivSave.isEnabled = click == 0
            }
        } }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditCategoryAdapterViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.check_styles,
            parent, false)
        return EditCategoryAdapterViewHolder(itemView) }
    override fun onBindViewHolder(holder: EditCategoryAdapterViewHolder, position: Int) {
        holder.bind(position) }
    override fun getItemCount() = checkedList.size
}