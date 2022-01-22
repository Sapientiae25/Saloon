package com.example.saloon

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import java.util.*

class CategoryFragment : Fragment(){

    lateinit var accountItem: AccountItem
    lateinit var categoryItem: CategoryItem
    private lateinit var communicator : ChangeFragment


    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getParcelable<AccountItem>("accountItem")?.let { accountItem = it }
        arguments?.getParcelable<CategoryItem>("categoryItem")?.let { categoryItem = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_category, container, false)
        activity!!.title = categoryItem.category
        val styleItemList = mutableListOf<StyleItem>()
        val rvCategoryStyleItems = rootView.findViewById<RecyclerView>(R.id.rvCategoryStyleItems)
        val tvNoStyles = rootView.findViewById<TextView>(R.id.tvNoStyles)
        val ivStoreFront = rootView.findViewById<ImageView>(R.id.ivStoreFront)
        val btnNewStyle = rootView.findViewById<FloatingActionButton>(R.id.btnNewStyle)
        rvCategoryStyleItems.adapter = StyleItemAdapter(styleItemList,accountItem,(activity as DefaultActivity))
        rvCategoryStyleItems.layoutManager = LinearLayoutManager(context)
        val url = "http://192.168.1.102:8012/saloon/get_category_styles.php"
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                println(response)
                val arr = JSONArray(response)
                if (arr.length() == 0){tvNoStyles.visibility = View.VISIBLE
                    ivStoreFront.visibility = View.GONE;btnNewStyle.visibility = View.GONE}
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val name = obj.getString("name")
                    val price = obj.getString("price").toFloat()
                    val time = obj.getString("time")
                    val styleId = obj.getString("style_id")
                    val maxTime = obj.getString("max_time")
                    val info = obj.getString("info")
                    val timeItem = TimeItem(time,maxTime)
                    styleItemList.add(StyleItem(name,price,timeItem,info,id=styleId)) }
                rvCategoryStyleItems.adapter?.notifyItemRangeInserted(0,styleItemList.size)},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["category_id"] = categoryItem.id
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        return rootView }
    companion object {
        fun newInstance(param1: AccountItem,param2: CategoryItem) =
            CategoryFragment().apply {
                arguments = Bundle().apply { putParcelable("accountItem", param1)
                    putParcelable("categoryItem", param2) }} }
}
