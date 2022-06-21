package com.example.saloon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.json.JSONObject

class CreateCategoryFragment : Fragment(){

    lateinit var ivSave: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_create_category, container, false)
        (activity as DefaultActivity).supportActionBar?.title = "Create Category"
        val accountItem = (activity as DefaultActivity).accountItem
        val checkList = mutableListOf<CheckItem>()
        val etCategory = rootView.findViewById<TextView>(R.id.etCategory)
        var click = 0
        ivSave = rootView.findViewById(R.id.ivSave)
        val tvNoStyles = rootView.findViewById<TextView>(R.id.tvNoStyles)
        val llAddStyles = rootView.findViewById<LinearLayout>(R.id.llAddStyles)
//        rvAddStyles.layoutManager = LinearLayoutManager(context)
//        rvAddStyles.adapter = AddStyleAdapter(checkList,this)
//        rvAddStyles.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
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
                    checkList.add(CheckItem(styleId,name))
                    val check = CheckBox(context)
                    check.id = styleId.toInt()
                    check.text = name
                    check.textSize = 22f
                    check.setPadding(10,5,10,5)
                    check.setOnCheckedChangeListener { _, b ->
                        if (b) {click += 1} else {click -= 1}
                        if (click == 0) { ivSave.text = getString(R.string.skip) }else{ ivSave.text = getString(R.string.save) } }
                    llAddStyles.addView(check)
                    for (i in 0 until llAddStyles.childCount){
                        val u = llAddStyles.getChildAt(i) as CheckBox
                        Log.println(Log.ASSERT,"llAddStyles","${u.text} ${u.id}") }
                }
//                rvAddStyles.adapter?.notifyItemRangeInserted(0,checkList.size)
                                               },
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
                stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        Log.println(Log.ASSERT,"response",response)
                        val obj = JSONObject(response)
                        val categoryId = obj.getString("category_id")
                        val exist = obj.getString("exist")
                        if (exist != "0"){Toast.makeText(context,"Category Already Exists",Toast.LENGTH_SHORT).show()}
                        else{
                        for (i in 0 until llAddStyles.childCount){
                            val box = llAddStyles.getChildAt(i) as CheckBox
                            Log.println(Log.ASSERT,"llAddStyles","${box.text} ${box.id}")
                            if (box.isChecked){

//                        for (check in checkList){
//                            if (check.checked){
                                val url2 = getString(R.string.url,"category_style.php")
                                val stringRequest2 = object : StringRequest(
                                    Method.POST, url2, Response.Listener { response -> println(response) },
                                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                                    @Throws(AuthFailureError::class)
                                    override fun getParams(): Map<String, String> {
                                        val params = HashMap<String, String>()
                                        params["category_id"] = categoryId
//                                        params["style_id"] = check.id
                                        params["style_id"] = box.id.toString()
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