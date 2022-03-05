package com.example.saloon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.json.JSONObject

class CreateCategoryFragment : Fragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_create_category, container, false)
        (activity as DefaultActivity).supportActionBar?.title = "Create Category"
        val accountItem = (activity as DefaultActivity).accountItem
        val checkList = mutableListOf<CheckItem>()
        val etCategory = rootView.findViewById<TextView>(R.id.etCategory)
        val ivSave = rootView.findViewById<ImageView>(R.id.ivSave)
        val tvNoStyles = rootView.findViewById<TextView>(R.id.tvNoStyles)
        val rvAddStyles = rootView.findViewById<RecyclerView>(R.id.rvAddStyles)
        rvAddStyles.layoutManager = LinearLayoutManager(context)
        rvAddStyles.adapter = AddStyleAdapter(checkList)
        var url = getString(R.string.url,"get_style.php")
        var stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                println(response)
                val arr = JSONArray(response)
                if (arr.length() == 0){tvNoStyles.visibility = View.VISIBLE}
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val name = obj.getString("name")
                    val styleId = obj.getString("style_id")
                    checkList.add(CheckItem(styleId,name)) }
                rvAddStyles.adapter?.notifyItemRangeInserted(0,checkList.size)},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["account_id"] = accountItem.id
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        ivSave.setOnClickListener { view ->
            if (etCategory.text.isEmpty()){etCategory.error = "Please Enter A Category Name"}
            else{
                url = getString(R.string.url,"create_category.php")
                val url2 = getString(R.string.url,"category_style.php")
                stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        println(response)
                        val obj = JSONObject(response)
                        val categoryId = obj.getString("category_id")
                        val exist = obj.getString("exist")
                        if (exist != "0"){Toast.makeText(context,"Category Already Exists",Toast.LENGTH_SHORT).show()}
                        else{
                        for (check in checkList){
                            if (check.checked){
                                val stringRequest2 = object : StringRequest(
                                    Method.POST, url2, Response.Listener { response -> println(response) },
                                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                                    @Throws(AuthFailureError::class)
                                    override fun getParams(): Map<String, String> {
                                        val params = HashMap<String, String>()
                                        params["category_id"] = categoryId
                                        params["style_id"] = check.id
                                        return params }}
                                VolleySingleton.instance?.addToRequestQueue(stringRequest2) } }}
                        Toast.makeText(context,"Category Created",Toast.LENGTH_SHORT).show()
                        view.findNavController().navigate(R.id.action_createCategory_to_saloonFragment)                                                        },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["category"] = etCategory.text.toString()
                        params["account_fk"] = accountItem.id
                        return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest) }
        }
        return rootView }

}