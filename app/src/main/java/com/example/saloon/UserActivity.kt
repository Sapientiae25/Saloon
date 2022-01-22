package com.example.saloon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import java.util.*

class UserActivity : Fragment() {

    lateinit var accountItem: AccountItem

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getParcelable<AccountItem>("accountItem")?.let {
            accountItem = it
        }
    }
    companion object {
        fun newInstance(param1: AccountItem) =
            UserActivity().apply {
                arguments = Bundle().apply {
                    putParcelable("accountItem", param1)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.activity_user, container, false)
        val styleItemList = mutableListOf<StyleItem>()
        val rvStyleItems = rootView.findViewById<RecyclerView>(R.id.rvStyleItems)
        val rvStyleCategories = rootView.findViewById<RecyclerView>(R.id.rvStyleCategories)
        val categoryList = mutableListOf<CategoryItem>(CategoryItem("",""))
        rvStyleCategories.adapter = StyleCategoryAdapter(categoryList,(activity as DefaultActivity),accountItem)
        rvStyleCategories.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
        rvStyleItems.adapter = StyleItemAdapter(styleItemList,accountItem,(activity as DefaultActivity))
        rvStyleItems.layoutManager = LinearLayoutManager(context)
        val btnNewStyle = rootView.findViewById<FloatingActionButton>(R.id.btnNewStyle)
        val tvNoStyles = rootView.findViewById<TextView>(R.id.tvNoStyles)
        activity?.title = accountItem.name
        btnNewStyle.setOnClickListener {
            val intent = Intent(context, CreateStyleActivity::class.java)
            intent.putExtra("account_item", accountItem)
            startActivity(intent) }

        var url = "http://192.168.1.102:8012/saloon/get_categories.php"
        var stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                println(response)
                val arr = JSONArray(response)
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val category = obj.getString("category")
                    val categoryId = obj.getString("id")
                    categoryList.add(CategoryItem(categoryId,category)) }
                rvStyleCategories.adapter?.notifyItemRangeInserted(1,categoryList.size)},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["account_id"] = accountItem.id
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        url = "http://192.168.1.102:8012/saloon/get_style.php"
        stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                println(response)
                val arr = JSONArray(response)
                if (arr.length() == 0){tvNoStyles.visibility = View.VISIBLE}
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val tags = mutableListOf<String>()
                    val name = obj.getString("name")
                    val price = obj.getString("price").toFloat()
                    val time = obj.getString("time")
                    val styleId = obj.getString("style_id")
                    val maxTime = obj.getString("max_time")
                    val info = obj.getString("info")
                    val tagArray = obj.getJSONArray("tags")
                    for (y in 0 until tagArray.length()){
                        val tag = tagArray.getString(y); tags.add(tag)}
                    val timeItem = TimeItem(time,maxTime)
                    styleItemList.add(StyleItem(name,price,timeItem,info,tags,styleId)) }
                rvStyleItems.adapter?.notifyItemRangeInserted(0,styleItemList.size)},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["account_id"] = accountItem.id
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        return rootView
    }



//        navView.setNavigationItemSelectedListener {
//            when (it.itemId) {
//                R.id.miAccount -> {
//                    val intent = Intent(this, AccountActivity::class.java)
//                    intent.putExtra("account_item",accountItem)
//                    startActivity(intent)
//                }};true }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (toggle.onOptionsItemSelected((item))){
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }
}