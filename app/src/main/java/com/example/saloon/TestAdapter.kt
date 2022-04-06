package com.example.saloon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class TestAdapter (private val calendarArray: MutableList<CalendarItem>)
    : RecyclerView.Adapter<TestAdapter.TestViewHolder>(),CloseSheet{

    var bottomSheetFragment: CalendarBottomSheetFragment? = null

    inner class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvCalendar: TextView = itemView.findViewById(R.id.tvCalendar)
        fun bind(index: Int){
            val currentItem = calendarArray[index]
            tvCalendar.text = currentItem.date
            itemView.layoutParams.height = itemView.context.resources.getDimensionPixelSize(R.dimen.item_height) * currentItem.span
            if (currentItem.gone) { itemView.layoutParams.height = 0 }
            if (currentItem.calendarType != 2){
                itemView.setOnClickListener { }}
            else{
                itemView.setOnClickListener {
                    val bundle = Bundle()
                    val bookingBottomSheetFragment = StyleBottomSheet()
                    bundle.putParcelable("booking",currentItem)
                    bookingBottomSheetFragment.show((itemView.context as DefaultActivity).supportFragmentManager, "BottomSheetDialog")
                    bookingBottomSheetFragment.arguments = bundle }}
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.calendar_cell_layout,
            parent, false)
        return TestViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        val currentItem = calendarArray[position]
        holder.bind(position)
        val item = holder.itemView
        val tvCalendar = holder.tvCalendar
//        when (currentItem.calendarType) {
//            1 -> {tvCalendar.setBackgroundResource(R.drawable.red_round) ;tvCalendar.textSize = 18f
//                tvCalendar.text = item.context.getString(R.string.address_ph,currentItem.start,currentItem.end) }
//            2 -> {tvCalendar.setBackgroundResource(R.drawable.blue_round) ;tvCalendar.textSize = 18f
//                tvCalendar.text = item.context.getString(R.string.obj_colon,currentItem.name,
//                    item.context.getString(R.string.address_ph,currentItem.start,currentItem.end))
//            }
//            else -> {item.setBackgroundResource(R.drawable.border);tvCalendar.text = ""} }
    }
    override fun getItemCount() = calendarArray.size
    override fun close() {
        bottomSheetFragment?.dismiss()
    }


}