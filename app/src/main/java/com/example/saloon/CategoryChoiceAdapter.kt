package com.example.saloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView


class CategoryChoiceAdapter (private val checkedList: MutableList<CheckItem>,val fragment: ChooseCategoryFragment)
    : RecyclerView.Adapter<CategoryChoiceAdapter.CategoryChoiceViewHolder>(){

    inner class CategoryChoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val rbChecked: RadioButton = itemView.findViewById(R.id.rbChecked)
        private var click = 0

        fun bind(index: Int){
            val currentItem = checkedList[index]
            rbChecked.text = currentItem.style
            itemView.setOnClickListener{
                if (currentItem.checked){
                    rbChecked.isChecked = false
                    currentItem.checked = false
                    click -= 0
                }else{ click += 0
                    rbChecked.isChecked = true
                    currentItem.checked = true }
                if (click == 0) { fragment.ivSave.text = itemView.context.getString(R.string.skip)
                    fragment.ivSave.setBackgroundColor(itemView.context.getColor(R.color.light_grey)) }else{
                    fragment.ivSave.text = itemView.context.getString(R.string.save)
                    fragment.ivSave.setBackgroundColor(itemView.context.getColor(R.color.loginColor)) }
                fragment.ivSave.isEnabled = click == 0
            } } }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryChoiceViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.check_styles,
            parent, false)
        return CategoryChoiceViewHolder(itemView) }
    override fun onBindViewHolder(holder: CategoryChoiceViewHolder, position: Int) {
        holder.bind(position) }
    override fun getItemCount() = checkedList.size
}