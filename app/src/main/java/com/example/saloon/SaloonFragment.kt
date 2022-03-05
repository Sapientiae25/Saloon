package com.example.saloon

import android.app.Dialog
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.SearchView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class SaloonFragment : Fragment() {

    var displayStyleList = mutableListOf<StyleItem>()
    var styleItemList = mutableListOf<StyleItem>()
    lateinit var rvStyleItems: RecyclerView
    lateinit var tvNoStyles: TextView
    lateinit var accountItem: AccountItem
    private var back = 0
    private val background = ColorDrawable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_saloon, container, false)
        accountItem = (activity as DefaultActivity).accountItem
        (activity as DefaultActivity).supportActionBar?.title = accountItem.name
        back = arguments?.getInt("back")!!
        rvStyleItems = rootView.findViewById(R.id.rvStyleItems)
        val backgroundColor = ContextCompat.getColor(requireContext(),R.color.red)
        val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_delete_24)!!
        icon.setTint(ContextCompat.getColor(requireContext(),R.color.black))
        val inWidth = icon.intrinsicWidth
        val inHeight = icon.intrinsicHeight
        val rvStyleCategories = rootView.findViewById<RecyclerView>(R.id.rvStyleCategories)
        val tvAddress = rootView.findViewById<TextView>(R.id.tvAddress)
        val tvOpen = rootView.findViewById<TextView>(R.id.tvOpen)
        val tvRating = rootView.findViewById<TextView>(R.id.tvRating)
        val btnNewStyle = rootView.findViewById<FloatingActionButton>(R.id.btnNewStyle)
        val categoryList = mutableListOf(CategoryItem())
        val svStyle = rootView.findViewById<SearchView>(R.id.svStyle)
        rvStyleCategories.adapter = StyleCategoryAdapter(categoryList)
        rvStyleCategories.adapter?.notifyItemRangeInserted(1,categoryList.size)
        rvStyleCategories.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
        rvStyleItems.adapter = SaloonStyleAdapter(displayStyleList)
        rvStyleItems.layoutManager = LinearLayoutManager(context)
        rvStyleItems.adapter?.notifyItemRangeRemoved(0,displayStyleList.size)
        val btnFilter = rootView.findViewById<FloatingActionButton>(R.id.btnFilter)
        tvNoStyles = rootView.findViewById(R.id.tvNoStyles)
        activity?.title = accountItem.name
        tvRating.text = getString(R.string.rate,accountItem.rating)
        tvAddress.text = getString(R.string.comma,accountItem.addressItem?.address,accountItem.addressItem?.postcode)
        tvOpen.text = getString(R.string.separate,accountItem.open,accountItem.close)
        btnFilter.setOnClickListener { view -> view.findNavController().navigate(R.id.action_saloonFragment_to_filterFragment) }
        btnNewStyle.setOnClickListener { view -> view.findNavController().navigate(R.id.action_saloonFragment_to_createStyleFragment) }
        val ivStoreFront = rootView.findViewById<ImageSlider>(R.id.ivStoreFront)
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.ic_baseline_add_circle_24))
        imageList.add(SlideModel(R.drawable.trim,ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.trim,ScaleTypes.FIT))
        ivStoreFront.setImageList(imageList)
        ivStoreFront.setItemClickListener(object: ItemClickListener {override fun onItemSelected(position: Int) {addImage(position)}})
        val categoryTouchHelper = ItemTouchHelper(object:ItemTouchHelper.SimpleCallback(ItemTouchHelper.RIGHT or
                ItemTouchHelper.LEFT,0){
            override fun onMove(recyclerView: RecyclerView,viewHolder:RecyclerView.ViewHolder,target:RecyclerView.ViewHolder):Boolean {
                val sourcePosition = viewHolder.adapterPosition
                val targetPosition = target.adapterPosition
                Collections.swap(categoryList,sourcePosition,targetPosition)
                rvStyleCategories.adapter?.notifyItemMoved(sourcePosition,targetPosition)
                return true
            }override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {} })

        val styleTouchHelper = ItemTouchHelper(object:ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or
                ItemTouchHelper.DOWN,0){
            override fun onMove(recyclerView: RecyclerView,viewHolder:RecyclerView.ViewHolder,target:RecyclerView.ViewHolder):Boolean {
                val sourcePosition = viewHolder.adapterPosition
                val targetPosition = target.adapterPosition
                Collections.swap(displayStyleList,sourcePosition,targetPosition)
                rvStyleItems.adapter?.notifyItemMoved(sourcePosition,targetPosition)
                return true
            }override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                displayStyleList.removeAt(position)
                rvStyleItems.adapter?.notifyItemRemoved(position) }
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val swipeFlag = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                return makeMovementFlags(0,swipeFlag) }

            override fun onChildDraw(
                canvas: Canvas,recyclerView: RecyclerView,viewHolder: RecyclerView.ViewHolder,dX: Float,dY: Float,actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val itemHeight = itemView.bottom - itemView.top
                background.color = backgroundColor
                background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                background.draw(canvas)
                val iconTop = itemView.top + (itemHeight - inHeight) / 2
                val iconMargin = (itemHeight - inHeight) / 2
                val iconLeft = itemView.right - iconMargin - inWidth
                val iconRight = itemView.right - iconMargin
                val iconBottom = iconTop + inHeight
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                icon.draw(canvas)
                super.onChildDraw(canvas,recyclerView,viewHolder,dX, dY,actionState,isCurrentlyActive) } })
        styleTouchHelper.attachToRecyclerView(rvStyleItems)
        categoryTouchHelper.attachToRecyclerView(rvStyleCategories)
        var url = getString(R.string.url,"get_categories.php")
        var stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                Log.println(Log.ASSERT,"cat",response)
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
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        if (back == 0){
            url = getString(R.string.url,"saloon_get_style.php")
            stringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { response ->
                    Log.println(Log.ASSERT,"SUI", response)
                    val arr = JSONArray(response)
                    if (arr.length() == 0){tvNoStyles.visibility = View.VISIBLE}
                    for (x in 0 until arr.length()){
                        val obj = arr.getJSONObject(x)
                        val name = obj.getString("name")
                        val price = obj.getString("price").toFloat()
                        val time = obj.getString("time")
                        val styleId = obj.getString("style_id")
                        val maxTime = obj.getString("max_time")
                        val visible = obj.getInt("privacy") == 1
                        val info = obj.getString("info")
                        val rating = obj.getString("rating").toFloatOrNull()
                        val timeItem = TimeItem(time,maxTime)
                        styleItemList.add(StyleItem(name,price,timeItem,info,styleId,rating=rating,accountItem=accountItem,privacy=visible)) }
                    displayStyleList.addAll(styleItemList)
                    rvStyleItems.adapter?.notifyItemRangeInserted(0,displayStyleList.size)},
                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["account_id"] = accountItem.id
                    return params
                }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest)}
        else if (back == 1){
            val filterItem = accountItem.filterItem
            val filterObj = JSONObject()
            val length = JSONArray(filterItem.length)
            val gender = JSONArray(filterItem.gender)
            filterObj.put("length",length)
            filterObj.put("gender",gender)
            filterObj.put("account_id",accountItem.id)
            val filterArr = JSONArray()
            filterArr.put(filterObj)
            url = getString(R.string.url,"filter_account.php")
            val jsonRequest = JsonArrayRequest(
                Request.Method.POST, url,filterArr, { arr ->
                    Log.println(Log.ASSERT,"array",arr.toString())
                    if (arr.length() == 0){tvNoStyles.visibility = View.VISIBLE}
                    for (x in 0 until arr.length()){
                        val obj = arr.getJSONObject(x)
                        val name = obj.getString("name")
                        val price = obj.getString("price").toFloat()
                        val time = obj.getString("time")
                        val styleId = obj.getString("style_id")
                        val maxTime = obj.getString("max_time")
                        val info = obj.getString("info")
                        val timeItem = TimeItem(time,maxTime)
                        styleItemList.add(StyleItem(name,price,timeItem,info,styleId,accountItem=accountItem)) }
                    displayStyleList.addAll(styleItemList)
                    rvStyleItems.adapter?.notifyItemRangeInserted(0,displayStyleList.size) },
                { volleyError -> println(volleyError.message) })
            VolleySingleton.instance?.addToRequestQueue(jsonRequest) }

        svStyle.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()){
                    rvStyleItems.adapter?.notifyItemRangeRemoved(0,displayStyleList.size)
                    displayStyleList.clear()
                    val search = newText.lowercase(Locale.getDefault())
                    for (style in styleItemList) { if (style.name.lowercase(Locale.getDefault()).contains(search))
                    { displayStyleList.add(style) } }
                    rvStyleItems.adapter?.notifyItemRangeInserted(0,displayStyleList.size)
                    if (displayStyleList.size == 0){tvNoStyles.visibility = View.VISIBLE}
                }else{
                    rvStyleItems.adapter?.notifyItemRangeRemoved(0,displayStyleList.size)
                    displayStyleList.clear()
                    displayStyleList.addAll(styleItemList)
                    rvStyleItems.adapter?.notifyItemRangeInserted(0,displayStyleList.size)
                }
                return true } })
        return rootView
    }
    private fun addImage(index: Int) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.add_image_layout)
        val remove = dialog.findViewById<TextView>(R.id.remove)
        val add = dialog.findViewById<TextView>(R.id.add)
        if (index == 0){remove.visibility = View.GONE}

        remove.setOnClickListener { dialog.dismiss() }
        add.setOnClickListener {dialog.dismiss(); }
        dialog.show() }
}