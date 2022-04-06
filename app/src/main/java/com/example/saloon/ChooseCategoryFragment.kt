package com.example.saloon

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChooseCategoryFragment : Fragment() {

    lateinit var styleItem: StyleItem
    private lateinit var imageList: ArrayList<Bitmap>
    lateinit var ivSave: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_choose_category, container, false)
        styleItem = arguments?.getParcelable("styleItem")!!
        imageList = arguments?.getParcelableArrayList("imageList")!!
        val filterItem = styleItem.filterItem
        val accountItem = (activity as DefaultActivity).accountItem
        val rvChooseCategory = rootView.findViewById<RecyclerView>(R.id.rvChooseCategory)
        ivSave = rootView.findViewById(R.id.ivSave)
        val categoryList = mutableListOf<CheckItem>()
        rvChooseCategory.layoutManager = LinearLayoutManager(context)
        rvChooseCategory.adapter = CategoryChoiceAdapter(categoryList,this)
        rvChooseCategory.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        var url = getString(R.string.url,"get_categories.php")
        var stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                println(response)
                val arr = JSONArray(response)
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val category = obj.getString("category")
                    val categoryId = obj.getString("id")
                    categoryList.add(CheckItem(categoryId,category)) }
                rvChooseCategory.adapter?.notifyItemRangeInserted(0,categoryList.size)},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["account_id"] = accountItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        ivSave.setOnClickListener { view ->
            url = getString(R.string.url,"create_style.php")
            stringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { response ->
                    val styleId = response
                    val url3 = getString(R.string.url,"delete_tag.php")
                    val stringRequest3 = object : StringRequest(
                        Method.POST, url3, Response.Listener {},
                        Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                        @Throws(AuthFailureError::class)
                        override fun getParams(): Map<String, String> {
                            val params = HashMap<String, String>()
                            params["style_id"] = styleId
                            return params }}
                    VolleySingleton.instance?.addToRequestQueue(stringRequest3)
                    val url2 = getString(R.string.url,"set_filters.php")
                    val stringRequest2 = object : StringRequest(
                        Method.POST, url2, Response.Listener {response -> println(response)},
                        Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                        @Throws(AuthFailureError::class)
                        override fun getParams(): Map<String, String> {
                            val params = HashMap<String, String>()
                            params["style_fk"] = styleId
                            params["gender"] = filterItem.gender.toString()
                            params["length"] = filterItem.length.toString()
                            return params }}
                    VolleySingleton.instance?.addToRequestQueue(stringRequest2)
                    val url4 = getString(R.string.url,"category_style.php")
                    for (category in categoryList){
                        if (category.checked){
                            val stringRequest4 = object : StringRequest(
                                Method.POST, url4, Response.Listener { response -> println(response)},
                                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                                @Throws(AuthFailureError::class)
                                override fun getParams(): Map<String, String> {
                                    val params = HashMap<String, String>()
                                    params["category_id"] = category.id
                                    params["style_id"] = styleId
                                    params["account_fk"] = accountItem.id
                                    return params }}
                            VolleySingleton.instance?.addToRequestQueue(stringRequest4) }}
                    uploadImages(styleId)
                    styleItem.id = styleId
                    val bundle = bundleOf(Pair("styleItem",styleItem))
                    view.findNavController().navigate(R.id.action_chooseCategoryFragment_to_styleFragment,bundle)
                }, Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["name"] = styleItem.name
                params["price"] = styleItem.price.toString()
                params["time"] = styleItem.time.time
                params["account_id"] = accountItem.id
                params["info"] = styleItem.info
                return params
            }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
        return rootView
}
    private fun bitMapToString(bitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val b = bytes.toByteArray()
        return Base64.getEncoder().encodeToString(b) }
    private fun uploadImages(styleId: String){
        val url = getString(R.string.url,"create_style_image.php")
        for (image in imageList){
            val stringImage = bitMapToString(image)
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { },
                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = java.util.HashMap<String, String>()
                    params["image"] = stringImage
                    params["style_id"] = styleId
                    return params }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest) }}
}