package com.example.saloon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
        val rootView =  inflater.inflate(R.layout.fragment_choose_category, container, false)
        val rvChooseCategory = rootView.findViewById<RecyclerView>(R.id.rvChooseCategory)
        val ivSave = rootView.findViewById<ImageView>(R.id.ivSave)
        val categoryList = mutableListOf<CategoryItem>()
        rvChooseCategory.layoutManager = LinearLayoutManager(context)

        val url = "http://192.168.1.102:8012/saloon/get_categories.php"
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                println(response)
                val arr = JSONArray(response)
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val category = obj.getString("category")
                    val categoryId = obj.getString("id")
                    categoryList.add(CategoryItem(categoryId,category)) }
                rvChooseCategory.adapter?.notifyItemRangeInserted(0,categoryList.size)},
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
}