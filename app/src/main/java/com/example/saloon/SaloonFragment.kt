package com.example.saloon

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class SaloonFragment : Fragment() {

    var displayStyleList = mutableListOf<StyleItem>()
    var styleItemList = mutableListOf<StyleItem>()
    lateinit var rvStyleItems: RecyclerView
    lateinit var tvNoStyles: TextView
    lateinit var accountItem: AccountItem
    private lateinit var ivStoreFront: ImageSlider
    private lateinit var imageList: ArrayList<SlideModel>
    private val imageUrls = mutableListOf<String>()
    private var back = 0
    private val background = ColorDrawable()
    lateinit var directory: File
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private lateinit var startGalleryForResult: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_saloon, container, false)
        accountItem = (activity as DefaultActivity).accountItem
        (activity as DefaultActivity).supportActionBar?.title = accountItem.name
        back = arguments?.getInt("back")!!
        rvStyleItems = rootView.findViewById(R.id.rvStyleItems)
        directory = ContextWrapper(context).getDir("imageDir", Context.MODE_PRIVATE)
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
        rvStyleCategories.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
        rvStyleItems.layoutManager = LinearLayoutManager(context)
        val btnFilter = rootView.findViewById<FloatingActionButton>(R.id.btnFilter)
        tvNoStyles = rootView.findViewById(R.id.tvNoStyles)
        tvRating.text = getString(R.string.rate,accountItem.rating)
        tvAddress.text = getString(R.string.comma,accountItem.addressItem?.address,accountItem.addressItem?.postcode)
        tvOpen.text = getString(R.string.separate,accountItem.open,accountItem.close)
        btnFilter.setOnClickListener { view -> view.findNavController().navigate(R.id.action_saloonFragment_to_filterFragment) }
        btnNewStyle.setOnClickListener { view -> view.findNavController().navigate(R.id.action_saloonFragment_to_createStyleFragment) }
        ivStoreFront = rootView.findViewById(R.id.ivStoreFront)
        imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.add,ScaleTypes.CENTER_INSIDE))
        ivStoreFront.setImageList(imageList)
        ivStoreFront.setItemClickListener(object: ItemClickListener {override fun onItemSelected(position: Int) {
            if (imageList.size == 1) {photoOption()} else {addImage(position)}
        }})
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
                        styleItemList.add(StyleItem(name,price,timeItem,info,styleId,rating=rating,accountItem=accountItem,privacy=visible))}
                    displayStyleList.addAll(styleItemList)
                    rvStyleItems.adapter = SaloonStyleAdapter(displayStyleList)
//                    rvStyleItems.adapter?.notifyItemRangeInserted(0,displayStyleList.size)
                                                    },
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
//                    rvStyleItems.adapter?.notifyItemRangeInserted(0,displayStyleList.size)
                    rvStyleItems.adapter = SaloonStyleAdapter(displayStyleList)},
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
        startGalleryForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val takenImage = intent?.extras?.get("data") as Bitmap
//                saveToInternalStorage(takenImage)
                ivStoreFront.setImageList(imageList) } }
        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val takenImage = intent?.extras?.get("data") as Bitmap
//                saveToInternalStorage(takenImage)
                ivStoreFront.setImageList(imageList) } }
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
        remove.setOnClickListener { deleteImage(imageUrls[index]); dialog.dismiss() }
        add.setOnClickListener {photoOption(); dialog.dismiss()}
        dialog.show() }
    private fun photoOption(){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.pick_photo)
        val gallery = dialog.findViewById<CardView>(R.id.gallery)
        val camera = dialog.findViewById<CardView>(R.id.camera)
        gallery.setOnClickListener {openGallery(); dialog.dismiss() }
        camera.setOnClickListener {dispatchTakePictureIntent(); dialog.dismiss()}
        dialog.show() }
    private fun openGallery(){
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startGalleryForResult.launch(gallery)
    }
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try { startForResult.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Unable to open camera",Toast.LENGTH_SHORT).show() } }
    private fun saveToInternalStorage(bitmapImage: Bitmap): String? {
        var fos: FileOutputStream? = null
        var imageId = ""
        try {
            val url = getString(R.string.url,"saloon_image_url.php")
            val stringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { response -> Log.println(Log.ASSERT,"IMG", response)
                    imageId = "${response}.jpg"
                    val path = File(directory, imageId)
                    fos = FileOutputStream(path)
                    bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
//                    loadImages()
                                                    },
                Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["account_id"] = accountItem.id
                    return params }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest)
        }catch (e:Exception){e.printStackTrace();deleteImage(imageId)} finally{try{fos?.close()}catch(e:IOException){e.printStackTrace()}}
        return directory.absolutePath
    }
    private fun deleteImage(imageId: String){
        val url = getString(R.string.url,"delete_saloon_image.php")
        val stringRequest = object : StringRequest(Method.POST, url, Response.Listener {
            val file = File(directory, imageId)
            if (file.delete()){ Log.println(Log.ASSERT,"DEL", "DELETED") }
            else {Log.println(Log.ASSERT,"DEL", "FAIL TO DELETE")}
        },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
        override fun getParams(): Map<String, String> {
            val params = HashMap<String, String>()
            params["image_id"] = imageId
            return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest) }
    private fun loadImages(){
        val url = getString(R.string.url,"get_saloon_images.php")
        val stringRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->
            val arr = JSONArray(response)
            for (i in 0 until arr.length()){
                val imageId = "${i}.jpg"
                imageUrls.add(imageId)
                try {
                    val file = File(directory, imageId)
                    imageList.add(SlideModel(file.absolutePath,ScaleTypes.FIT))
                } catch (e: FileNotFoundException) {
                    e.printStackTrace() } }
            ivStoreFront.setImageList(imageList) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
        override fun getParams(): Map<String, String> {
            val params = HashMap<String, String>()
            params["account_id"] = accountItem.id
            return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
}