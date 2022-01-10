package com.example.saloon

import android.content.ClipDescription
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TestAdapter (val nums: MutableList<Int>)
    : RecyclerView.Adapter<TestAdapter.TestViewHolder>() {

    inner class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val llTest: LinearLayout = itemView.findViewById(R.id.llTest)
//        private val tvTest: TextView = itemView.findViewById(R.id.tvTest)

        fun bind(index: Int){
//            tvTest.text = nums[index].toString()
            val dragListListener = View.OnDragListener { view, event ->
                when (event.action){
                    DragEvent.ACTION_DRAG_STARTED -> {
                        event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                    }
                    DragEvent.ACTION_DRAG_ENTERED -> {
                        view.invalidate()
                        true
                    }
                    DragEvent.ACTION_DRAG_LOCATION -> {
                        true
                    }
                    DragEvent.ACTION_DRAG_EXITED -> {
                        view.invalidate()
                        true
                    }
                    DragEvent.ACTION_DROP -> {
                        val item = event.clipData.getItemAt(0)
                        val dragData = item.text
                        println("worked")

                        val v = event.localState as View
                        val owner = v.parent as ViewGroup
                        owner.removeView(v)
                        val destination = view as LinearLayout
                        println(nums[index])

                        destination.addView(v)
                        v.visibility = View.VISIBLE
                        true
                    }
                    DragEvent.ACTION_DRAG_ENDED -> {
                        view.invalidate()
                        true
                    }
                    else -> false

                }
            }
            llTest.setOnDragListener(dragListListener)

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.test_rv,
            parent, false)
        return TestViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount() = nums.size
}