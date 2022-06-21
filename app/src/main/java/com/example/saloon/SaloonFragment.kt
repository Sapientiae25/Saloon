package com.example.saloon

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.math.abs


class SaloonFragment : Fragment() {

    var displayStyleList = mutableListOf<StyleItem>()
    var styleItemList = mutableListOf<StyleItem>()
    lateinit var rvStyleItems: RecyclerView
    lateinit var rvStyleCategories: RecyclerView
    var categoryList = mutableListOf(CategoryItem())
    lateinit var tvNoStyles: TextView
    lateinit var accountItem: AccountItem
    private var back = 0
    private val background = ColorDrawable()
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private lateinit var startGalleryForResult: ActivityResultLauncher<Intent>
    private lateinit var vpImages: ViewPager2
    private val imageUrls = mutableListOf("")

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
        rvStyleCategories = rootView.findViewById(R.id.rvStyleCategories)
        val tvAddress = rootView.findViewById<TextView>(R.id.tvAddress)
        val tvOpen = rootView.findViewById<TextView>(R.id.tvOpen)
        val tvRating = rootView.findViewById<TextView>(R.id.tvRating)
        val btnNewStyle = rootView.findViewById<FloatingActionButton>(R.id.btnNewStyle)
        val svStyle = rootView.findViewById<SearchView>(R.id.svStyle)
        val swipeRefresh = rootView.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        rvStyleCategories.adapter = StyleCategoryAdapter(categoryList)
        rvStyleCategories.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
        rvStyleItems.layoutManager = LinearLayoutManager(context)
        rvStyleItems.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        rvStyleItems.isNestedScrollingEnabled = false
        val btnFilter = rootView.findViewById<FloatingActionButton>(R.id.btnFilter)
        tvNoStyles = rootView.findViewById(R.id.tvNoStyles)
        tvRating.text = getString(R.string.rate,accountItem.rating)
        tvAddress.text = getString(R.string.comma,accountItem.addressItem?.address,accountItem.addressItem?.postcode)
        tvOpen.text = getString(R.string.separate,accountItem.open,accountItem.close)
        btnFilter.setOnClickListener { view -> view.findNavController().navigate(R.id.action_saloonFragment_to_filterFragment) }
        btnNewStyle.setOnClickListener { view -> view.findNavController().navigate(R.id.action_saloonFragment_to_createStyleFragment) }

        vpImages = rootView.findViewById(R.id.vpImages)
        val sliderHandler = Handler(Looper.getMainLooper())
        val tabLayout = rootView.findViewById<TabLayout>(R.id.tabLayout)
        vpImages.adapter = SaloonImageAdapter(imageUrls) {index -> addImage(index) }
        vpImages.clipChildren = false
        vpImages.clipToPadding = false
        vpImages.offscreenPageLimit = 3
        vpImages.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position -> val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f }

        vpImages.setPageTransformer(compositePageTransformer)
        val sliderRunnable = Runnable {vpImages.currentItem = if(vpImages.currentItem+1 == imageUrls.size) 0
        else vpImages.currentItem+1}

        TabLayoutMediator(tabLayout,vpImages) { _, _ -> }.attach()

        vpImages.registerOnPageChangeCallback( object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 4000) } })
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
                val swipeFlag = ItemTouchHelper.LEFT
                return makeMovementFlags(0,swipeFlag) }

            override fun onChildDraw(
                canvas: Canvas,recyclerView: RecyclerView,viewHolder: RecyclerView.ViewHolder,dX: Float,dY: Float,actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
//                val itemHeight = itemView.bottom - itemView.top
                background.color = backgroundColor
                background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                background.draw(canvas)
//                val iconTop = itemView.top + (itemHeight - inHeight) / 2
//                val iconMargin = (itemHeight - inHeight) / 2
//                val iconLeft = itemView.right - iconMargin - inWidth
//                val iconRight = itemView.right - iconMargin
//                val iconBottom = iconTop + inHeight
//                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
//                icon.draw(canvas)
                super.onChildDraw(canvas,recyclerView,viewHolder,dX, dY,actionState,isCurrentlyActive) } })
        styleTouchHelper.attachToRecyclerView(rvStyleItems)

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
        startGalleryForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val takenImage = intent?.extras?.get("data") as Bitmap
                val stringImage = bitMapToString(takenImage)
                uploadImage(stringImage) } }
        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val takenImage = intent?.extras?.get("data") as Bitmap
                val stringImage = bitMapToString(takenImage)
                uploadImage(stringImage)} }
        loadData()
        loadImages()
        swipeRefresh.setOnRefreshListener { loadData();loadImages();swipeRefresh.isRefreshing = false }
        return rootView
    }
    private fun addImage(index: Int) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.add_image_layout)
        val remove = dialog.findViewById<TextView>(R.id.remove)
        val add = dialog.findViewById<TextView>(R.id.add)
        if (index == 0){if (imageUrls.size == 5) dialog.dismiss(); remove.visibility = View.GONE}
        else if (imageUrls.size == 5)  add.visibility = View.GONE
        remove.setOnClickListener { deleteImage(imageUrls[index]); dialog.dismiss() }
        add.setOnClickListener {photoOption(); dialog.dismiss()}
        dialog.show() }
    private fun photoOption(){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.pick_photo)
        val gallery = dialog.findViewById<CardView>(R.id.gallery)
        val camera = dialog.findViewById<CardView>(R.id.camera)
        gallery.setOnClickListener {openGallery(); dialog.dismiss() }
        camera.setOnClickListener {dispatchTakePictureIntent(); dialog.dismiss()}
        dialog.show() }
    private fun openGallery(){
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startGalleryForResult.launch(gallery) }
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try { startForResult.launch(takePictureIntent) } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Unable to open camera",Toast.LENGTH_SHORT).show() } }
    private fun deleteImage(imageId: String){
        val index = imageUrls.indexOf(imageId)
        imageUrls.removeAt(index)
        vpImages.adapter?.notifyItemRemoved(index)
        val url = getString(R.string.url,"delete_saloon_image.php")
        val stringRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->
            Toast.makeText(context,response,Toast.LENGTH_SHORT).show()},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
        override fun getParams(): Map<String, String> {
            val params = HashMap<String, String>()
            params["url"] = imageId
            return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest) }
    private fun loadImages(){
        imageUrls.clear(); imageUrls.add("")
        vpImages.adapter = SaloonImageAdapter(imageUrls) {index -> addImage(index) }
        val url = getString(R.string.url,"get_saloon_images.php")
        val stringRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->
            val arr = JSONArray(response)
            for (i in 0 until arr.length()){
                val imageId = arr.getString(i)
                imageUrls.add(imageId)}
            vpImages.adapter?.notifyItemRangeInserted(1,imageUrls.size)},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
        override fun getParams(): Map<String, String> {
            val params = HashMap<String, String>()
            params["account_id"] = accountItem.id
            return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
    private fun bitMapToString(bitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val b = bytes.toByteArray()
        return Base64.getEncoder().encodeToString(b) }
    private fun uploadImage(image: String){
        val url = getString(R.string.url,"saloon_image_url.php")
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                imageUrls.add(response)
                vpImages.adapter?.notifyItemInserted(imageUrls.size)},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["image"] = image
                params["account_id"] = accountItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest) }
    private fun loadData(){
        val categorySize = categoryList.size
        val styleSize = displayStyleList.size
        categoryList = mutableListOf(CategoryItem())
        displayStyleList.clear()
        rvStyleItems.adapter?.notifyItemRangeRemoved(1,styleSize)
        rvStyleCategories.adapter?.notifyItemRangeRemoved(1,categorySize)
        rvStyleCategories.adapter = StyleCategoryAdapter(categoryList)

        var url = getString(R.string.url,"get_categories.php")
        var stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                val arr = JSONArray(response)
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val category = obj.getString("category")
                    val categoryId = obj.getString("id")
                    val imageId = obj.getString("image_id")
                    categoryList.add(CategoryItem(categoryId,category,imageId)) }
                rvStyleCategories.adapter?.notifyItemRangeInserted(1,categoryList.size) },
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
                        val imageId = obj.getString("image_id")
                        val timeItem = TimeItem(time,maxTime)
                        styleItemList.add(StyleItem(name,price,timeItem,info,styleId,rating=rating,accountItem=accountItem,
                            privacy=visible,imageId=imageId))}
                    displayStyleList.addAll(styleItemList)
                    rvStyleItems.adapter = SaloonStyleAdapter(displayStyleList) },
                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["account_id"] = accountItem.id
                    return params }}
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
                    if (arr.length() == 0){tvNoStyles.visibility = View.VISIBLE}
                    for (x in 0 until arr.length()){
                        val obj = arr.getJSONObject(x)
                        val name = obj.getString("name")
                        val price = obj.getString("price").toFloat()
                        val time = obj.getString("time")
                        val styleId = obj.getString("style_id")
                        val maxTime = obj.getString("max_time")
                        val info = obj.getString("info")
                        val imageId = obj.getString("image_id")
                        val timeItem = TimeItem(time,maxTime)
                        styleItemList.add(StyleItem(name,price,timeItem,info,styleId,accountItem=accountItem,imageId=imageId)) }
                    displayStyleList.addAll(styleItemList)
                    rvStyleItems.adapter = SaloonStyleAdapter(displayStyleList) },
                { volleyError -> println(volleyError.message) })
            VolleySingleton.instance?.addToRequestQueue(jsonRequest) }
    }
}