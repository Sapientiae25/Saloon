package com.example.saloon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray

class ChooseCategoryFragment : Fragment() {

    lateinit var styleItem: StyleItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_choose_category, container, false)
        styleItem = arguments?.getParcelable("styleItem")!!
        val filterItem = styleItem.filterItem
        val accountItem = (activity as DefaultActivity).accountItem
        val rvChooseCategory = rootView.findViewById<RecyclerView>(R.id.rvChooseCategory)
        val ivSave = rootView.findViewById<ImageView>(R.id.ivSave)
        val categoryList = mutableListOf<CheckItem>()
        rvChooseCategory.layoutManager = LinearLayoutManager(context)
        rvChooseCategory.adapter = CategoryChoiceAdapter(categoryList)
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
                    println("r $response")
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
                            return params
                        }}
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
}}