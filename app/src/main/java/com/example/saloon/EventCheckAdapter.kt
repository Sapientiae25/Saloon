package com.example.saloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest


class EventCheckAdapter (private val bookingArray: MutableList<CalendarItem>,val fragment: BreakCheckPopUp)
    : RecyclerView.Adapter<EventCheckAdapter.EventCheckViewHolder>(){

    inner class EventCheckViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val tvDelete: TextView = itemView.findViewById(R.id.tvDelete)

        fun bind(index: Int){
            val currentItem = bookingArray[index]
            val time = itemView.context.getString(R.string.address_ph,currentItem.start,currentItem.end)
            val text = itemView.context.getString(R.string.book_show,currentItem.name,time)
            tvDelete.text = text

            itemView.setOnClickListener {
                if (currentItem.calendarType == 1){
                    val url = itemView.context.getString(R.string.url,"delete_break.php")
                    val stringRequest = object : StringRequest(
                        Method.POST, url, Response.Listener { response -> println(response) },
                        Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                        @Throws(AuthFailureError::class)
                        override fun getParams(): Map<String, String> {
                            val params = HashMap<String, String>()
                            params["break_id"] = currentItem.id.toString()
                            return params }}
                    VolleySingleton.instance?.addToRequestQueue(stringRequest)
                    val removeId = bookingArray.indexOf(currentItem)
                    bookingArray.removeAt(removeId)
                    notifyItemRemoved(removeId)
                    notifyItemRangeChanged(0,bookingArray.size)
                    if (bookingArray.size == 0){ fragment.deletes() } } } } }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventCheckViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.event_exist_layout,
            parent, false)
        return EventCheckViewHolder(itemView) }
    override fun onBindViewHolder(holder: EventCheckViewHolder, position: Int) {
        holder.bind(position) }
    override fun getItemCount() = bookingArray.size
}