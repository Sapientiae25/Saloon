package com.example.saloon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.squareup.picasso.Picasso
import org.json.JSONObject

class ClickStyleImageAdapter(private var images: MutableList<Pair<String, String>>)
    : RecyclerView.Adapter<ClickStyleImageAdapter.ClickStyleImageViewHolder>(){

    inner class ClickStyleImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val image = itemView.findViewById<ImageView>(R.id.image)
        fun bind(index: Int){
//            if (index == 0){
//                image.setImageDrawable(AppCompatResources.getDrawable(itemView.context,R.drawable.ic_baseline_add_circle_24))
//            }else{
                val currentItem = images[index]
                Picasso.get().load(itemView.context.getString(
                    R.string.url,"style_images/${currentItem.first}.jpeg")).fit().centerCrop().into(image)
                image.setOnClickListener { view ->
                    val styleId = currentItem.second
                    val url = itemView.context.getString(R.string.url,"find_style.php")
                    val stringRequest = object : StringRequest(
                        Method.POST, url, Response.Listener { response ->
                            val obj = JSONObject(response)
                            val name = obj.getString("name")
                            val price = obj.getString("price").toFloat()
                            val time = obj.getString("time")
                            val maxTime = obj.getString("max_time")
                            val info = obj.getString("info")
                            val rating = obj.getString("rating").toFloatOrNull()
                            val timeItem = TimeItem(time,maxTime)
                            val styleItem = StyleItem(name,price,timeItem,info,styleId,rating=rating)
                            val bundle = bundleOf(Pair("styleItem",styleItem))
                            view.findNavController().navigate(R.id.action_categoryFragment_to_styleFragment,bundle) },
                        Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(
                        AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = java.util.HashMap<String, String>()
                        params["style_id"] = styleId
                        return params }}
                    VolleySingleton.instance?.addToRequestQueue(stringRequest)}
//            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClickStyleImageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.image_layout, parent, false)
        return ClickStyleImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClickStyleImageViewHolder, position: Int) { holder.bind(position) }
    override fun getItemCount() = images.size
}