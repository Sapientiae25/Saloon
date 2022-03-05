package com.example.saloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView


class AddStyleAdapter (private val checkedList: MutableList<CheckItem>)
    : RecyclerView.Adapter<AddStyleAdapter.AddStyleViewHolder>(){

    inner class AddStyleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val rbChecked: RadioButton = itemView.findViewById(R.id.rbChecked)

        fun bind(index: Int){
            val currentItem = checkedList[index]
            rbChecked.isChecked = currentItem.checked
            rbChecked.text = currentItem.style
                itemView.setOnClickListener{
                if (currentItem.checked){
                    rbChecked.isChecked = false
                    currentItem.checked = false
                }else{
                    rbChecked.isChecked = true
                    currentItem.checked = true
                }
            }
        } }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddStyleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.check_styles,
            parent, false)
        return AddStyleViewHolder(itemView) }
    override fun onBindViewHolder(holder: AddStyleViewHolder, position: Int) {
        holder.bind(position) }
    override fun getItemCount() = checkedList.size
}