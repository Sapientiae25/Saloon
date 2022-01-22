package com.example.saloon

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


//class TestAdapter(val list: MutableList<Int>) : BaseAdapter (){
//    override fun getView(position:Int, convertView: View?, parent: ViewGroup?):View{
//        // Inflate the custom view
//        val inflater = parent?.context?.
//        getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val view = inflater.inflate(R.layout.test_rv,parent,false)
//
//        return view
//    }
//    override fun getItem(position: Int): Any {
//        return list[position]
//    }
//    override fun getItemId(position: Int): Long {
//        return position.toLong()
//    }
//    override fun getCount(): Int {
//        return list.size
//    }
//
//}
class TestAdapter (val nums: MutableList<Int>)
    : RecyclerView.Adapter<TestAdapter.TestViewHolder>() {

    inner class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        private val tvTest: TextView = itemView.findViewById(R.id.tvTest)
//
//        fun bind(index: Int){
//            println(tvTest.text)
//            tvTest.text = nums[index].toString()
//            tvTest.setOnClickListener { println("clicl") }
//            if (index == 10){
//                tvTest.visibility = View.GONE
//            }
//        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.test_rv,
            parent, false)
        return TestViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
//        holder.bind(position)
    }
    override fun getItemCount() = nums.size
}