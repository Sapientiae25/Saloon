package com.example.saloon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray

class CategoryFragment : Fragment(){

    lateinit var categoryItem: CategoryItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_category, container, false)
        val accountItem = (activity as DefaultActivity).accountItem
        categoryItem = arguments?.getParcelable("categoryItem")!!
        (activity as DefaultActivity).supportActionBar?.title = categoryItem.category
        val styleItemList = mutableListOf<StyleItem>()
        val rvCategoryStyleItems = rootView.findViewById<RecyclerView>(R.id.rvCategoryStyleItems)
        val tvNoStyles = rootView.findViewById<TextView>(R.id.tvNoStyles)
        val btnEditCategory = rootView.findViewById<FloatingActionButton>(R.id.btnEditCategory)
        rvCategoryStyleItems.adapter = CategoryAdapter(styleItemList)
        rvCategoryStyleItems.layoutManager = LinearLayoutManager(context)
        val ivStoreFront = rootView.findViewById<ImageSlider>(R.id.ivStoreFront)
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.trim, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.trim, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.trim, ScaleTypes.FIT))
        ivStoreFront.setImageList(imageList)
        val url = getString(R.string.url,"get_category_styles.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                Log.println(Log.ASSERT,"gory",response)
                val arr = JSONArray(response)
                if (arr.length() == 0){tvNoStyles.visibility = View.VISIBLE
                    ivStoreFront.visibility = View.GONE}
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val name = obj.getString("name")
                    val price = obj.getString("price").toFloat()
                    val time = obj.getString("time")
                    val styleId = obj.getString("style_id")
                    val maxTime = obj.getString("max_time")
                    val info = obj.getString("info")
                    val rating = obj.getString("rating").toFloatOrNull()
                    val timeItem = TimeItem(time,maxTime)
                    styleItemList.add(StyleItem(name,price,timeItem,info,id=styleId,rating=rating,accountItem=accountItem)) }
                rvCategoryStyleItems.adapter?.notifyItemRangeInserted(0,styleItemList.size)},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["category_id"] = categoryItem.id
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        btnEditCategory.setOnClickListener{ view ->
            val bundle = bundleOf(Pair("categoryItem",categoryItem))
            view.findNavController().navigate(R.id.action_categoryFragment_to_editCategoryFragment,bundle)
        }
        return rootView }
}
