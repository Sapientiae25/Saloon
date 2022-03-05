package com.example.saloon

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray

class EditCategoryFragment : Fragment(){

    lateinit var categoryItem: CategoryItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_edit_category, container, false)
        val accountItem = (activity as DefaultActivity).accountItem
        categoryItem = arguments?.getParcelable("categoryItem")!!
        val checkList = mutableListOf<CheckItem>()
        val etCategory = rootView.findViewById<TextView>(R.id.etCategory)
        val ivSave = rootView.findViewById<ImageView>(R.id.ivSave)
        val tvNoStyles = rootView.findViewById<TextView>(R.id.tvNoStyles)
        val rvAddStyles = rootView.findViewById<RecyclerView>(R.id.rvAddStyles)
        val btnDelete = rootView.findViewById<AppCompatButton>(R.id.btnDelete)
        rvAddStyles.layoutManager = LinearLayoutManager(context)
        rvAddStyles.adapter = AddStyleAdapter(checkList)
        etCategory.text = categoryItem.category
        var url = getString(R.string.url,"get_checked_styles.php")
        var stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                println(response)
                val arr = JSONArray(response)
                if (arr.length() == 0){tvNoStyles.visibility = View.VISIBLE}
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val exists = obj.getString("category_fk").isNotEmpty()
                    val name = obj.getString("name")
                    val styleId = obj.getString("style_id")
                    checkList.add(CheckItem(styleId,name,exists,exists)) }
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
                url = getString(R.string.url,"category_check.php")
                stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        if (response == "1"){Toast.makeText(context,"Category Already Exists",Toast.LENGTH_SHORT).show()}
                        else{
                            val url3 = getString(R.string.url,"category_style.php")
                            for (check in checkList){
                                if (check.checked != check.old && check.checked){
                                    val stringRequest3 = object : StringRequest(
                                        Method.POST, url3, Response.Listener { response -> println(response)},
                                        Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                                        @Throws(AuthFailureError::class)
                                        override fun getParams(): Map<String, String> {
                                            val params = HashMap<String, String>()
                                            params["category_id"] = categoryItem.id
                                            params["style_id"] = check.id
                                            return params }}
                                    VolleySingleton.instance?.addToRequestQueue(stringRequest3) }
                                else if (check.checked != check.old && !check.checked){
                                    val url2 = getString(R.string.url,"delete_category_style.php")
                                    val stringRequest2 = object : StringRequest(
                                        Method.POST, url2, Response.Listener { response -> println(response)},
                                        Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                                        @Throws(AuthFailureError::class)
                                        override fun getParams(): Map<String, String> {
                                            val params = HashMap<String, String>()
                                            params["category_id"] = categoryItem.id
                                            params["style_id"] = check.id
                                            return params }}
                                    VolleySingleton.instance?.addToRequestQueue(stringRequest2) }
                                Toast.makeText(context,"Category Updated",Toast.LENGTH_SHORT).show()
                                val bundle = bundleOf(Pair("categoryItem",categoryItem))
                                view.findNavController().navigate(R.id.categoryFragment,bundle)
                            }
                        } },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["category_id"] = categoryItem.id
                        return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
        }}

        btnDelete.setOnClickListener { checkDelete() }
        return rootView }
    private fun checkDelete() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.delete_check)
        val close = dialog.findViewById<TextView>(R.id.close)
        val delete = dialog.findViewById<TextView>(R.id.delete)
        val tvDeleteText = dialog.findViewById<TextView>(R.id.tvDeleteText)
        tvDeleteText.text = getString(R.string.delete_check,categoryItem.category)
        close.setOnClickListener { dialog.dismiss() }
        delete.setOnClickListener { view ->
            val url = getString(R.string.url,"delete_category.php")
            val stringRequest = object : StringRequest(
                Method.POST, url, Response.Listener {
                    view.findNavController().navigate(R.id.action_global_saloonFragment)
                    Toast.makeText(context,"Category Deleted",Toast.LENGTH_SHORT).show()},
                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["category_id"] = categoryItem.id
                    return params }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest)
            dialog.dismiss() }
        dialog.show()
    }
}