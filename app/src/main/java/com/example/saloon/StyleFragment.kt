package com.example.saloon

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.abs

class StyleFragment : Fragment() {

    private lateinit var accountItem : AccountItem
    private lateinit var styleItem : StyleItem
    private lateinit var timeItem : TimeItem
    private lateinit var timeValue: String
    private var privacy = true
    private lateinit var switchPrivacy: SwitchCompat
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private lateinit var startGalleryForResult: ActivityResultLauncher<Intent>
    private lateinit var vpImages: ViewPager2
    private val imageUrls = mutableListOf("")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_style, container, false)
        styleItem = arguments?.getParcelable("styleItem")!!
        (activity as DefaultActivity).supportActionBar?.title = styleItem.name
        accountItem = (activity as DefaultActivity).accountItem
        val tvDuration = rootView.findViewById<TextView>(R.id.tvDuration)
        val tvName = rootView.findViewById<TextView>(R.id.tvName)
        val tvEditStyle = rootView.findViewById<TextView>(R.id.tvEditStyle)
        val tvInfo = rootView.findViewById<TextView>(R.id.tvInfo)
        val tvPrice = rootView.findViewById<TextView>(R.id.tvPrice)
        switchPrivacy = rootView.findViewById(R.id.switchPrivacy)
        val tvAddress = rootView.findViewById<TextView>(R.id.tvAddress)
        val tvOpenHours = rootView.findViewById<TextView>(R.id.tvOpenHours)
        val llReviews = rootView.findViewById<LinearLayout>(R.id.llReviews)
        val styleRating = rootView.findViewById<RatingBar>(R.id.styleRating)
        val similarStyles = mutableListOf<StyleItem>()
        val rvMoreLike = rootView.findViewById<RecyclerView>(R.id.rvMoreLike)
        val btnBook = rootView.findViewById<AppCompatButton>(R.id.btnBook)
        val rvReviews = rootView.findViewById<RecyclerView>(R.id.rvReviews)
        val llMoreLikeThis = rootView.findViewById<LinearLayout>(R.id.llMoreLikeThis)
        val reviewList = mutableListOf<ReviewItem>()

        vpImages = rootView.findViewById(R.id.vpImages)
        val sliderHandler = Handler(Looper.getMainLooper())
        val tabLayout = rootView.findViewById<TabLayout>(R.id.tabLayout)
        val adapter = StyleImageAdapter(imageUrls) { index -> addImage(index) }
        vpImages.adapter = adapter
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
                sliderHandler.postDelayed(sliderRunnable, 2000) } })

        rvReviews.adapter = ReviewAdapter(reviewList)
        rvReviews.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
        tvAddress.text = (activity as DefaultActivity).accountItem.addressItem?.address
        tvOpenHours.text = getString(R.string.separate,accountItem.open,accountItem.close)
        timeItem = styleItem.time
        timeValue = if (timeItem.maxTime.isNullOrEmpty()) { timeItem.time } else {
            getString(R.string.time_distance, timeItem.time, timeItem.maxTime) }
        tvDuration.text = getString(R.string.duration_time,timeValue)
        tvPrice.text = getString(R.string.money,styleItem.price)
        btnBook.text = getString(R.string.separate,"BOOK NOW",tvPrice.text)
        tvName.text = styleItem.name
        tvInfo.text = styleItem.info
        llReviews.setOnClickListener { rvReviews.visibility = if (rvReviews.visibility == View.GONE){View.VISIBLE} else {View.GONE} }
        var url = getString(R.string.url,"get_reviews.php")
        var stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                val arr = JSONArray(response)
                var total = 0
                styleRating.rating = 0f
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val review = obj.getString("review")
                    val rating = obj.getInt("rating")
                    val reviewDate = obj.getString("date")
                    total += rating
                    reviewList.add(ReviewItem(review,rating,reviewDate)) }
                if (reviewList.size > 0) { val average = total / reviewList.size
                    styleRating.rating = average.toFloat()
                    rvReviews.adapter?.notifyItemRangeInserted(0, reviewList.size) } },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["style_id"] = styleItem.id
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        rvMoreLike.adapter = SimilarAdapter(similarStyles)
        url = getString(R.string.url,"saloon_get_style.php")
        stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                Log.println(Log.ASSERT,"POP",response)
                val arr = JSONArray(response)
                if (arr.length() == 0){llMoreLikeThis.visibility = View.GONE}
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val name = obj.getString("name")
                    val price = obj.getString("price").toFloat()
                    val time = obj.getString("time")
                    val styleId = obj.getString("style_id")
                    val maxTime = obj.getString("max_time")
                    val info = obj.getString("info")
                    val rating = obj.getString("rating").toFloatOrNull()
                    val timeItem = TimeItem(time,maxTime)
                    val imageId = obj.getString("image_id")
                    similarStyles.add(StyleItem(name,price,timeItem,info,styleId,rating=rating,imageId=imageId)) }
                rvMoreLike.adapter?.notifyItemRangeInserted(0,similarStyles.size) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["account_id"] = accountItem.id
                params["style_id"] = styleItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        url = getString(R.string.url,"check_style_privacy.php")
        stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                Log.println(Log.ASSERT,"s_priv",response)
                privacy = response == "1"
                changePrivacy()},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["style_id"] = styleItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        switchPrivacy.setOnClickListener { privacy = !switchPrivacy.isChecked; changePrivacy() }
        tvEditStyle.setOnClickListener{view ->
            val bundle = bundleOf(Pair("styleItem",styleItem))
            view.findNavController().navigate(R.id.action_styleFragment_to_editStyleFragment,bundle) }

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
        loadImages()

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
            Toast.makeText(context, "Unable to open camera", Toast.LENGTH_SHORT).show() } }
    private fun deleteImage(imageId: String){
        val index = imageUrls.indexOf(imageId)
        imageUrls.removeAt(index)
        vpImages.adapter?.notifyItemRemoved(index)
        val url = getString(R.string.url,"delete_style_image.php")
        val stringRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->
            Toast.makeText(context,response, Toast.LENGTH_SHORT).show()},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
        override fun getParams(): Map<String, String> {
            val params = java.util.HashMap<String, String>()
            params["url"] = imageId
            return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest) }
    private fun loadImages(){
        val url = getString(R.string.url,"get_style_images.php")
        val stringRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->
            val arr = JSONArray(response)
            for (i in 0 until arr.length()){
                val imageId = arr.getString(i)
                imageUrls.add(imageId)}
            vpImages.adapter?.notifyItemRangeInserted(1,imageUrls.size)},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
        override fun getParams(): Map<String, String> {
            val params = java.util.HashMap<String, String>()
            params["style_id"] = styleItem.id
            return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
    private fun bitMapToString(bitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val b = bytes.toByteArray()
        return Base64.getEncoder().encodeToString(b) }
    private fun uploadImage(image: String){
        val url = getString(R.string.url,"create_style_image.php")
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                imageUrls.add(response)
                vpImages.adapter?.notifyItemInserted(imageUrls.size)},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = java.util.HashMap<String, String>()
                params["image"] = image
                params["style_id"] = styleItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest) }
    private fun changePrivacy(){if (privacy){switchPrivacy.isChecked = !privacy; switchPrivacy.text = getString(R.string.priv)}
    else {switchPrivacy.isChecked = !privacy; switchPrivacy.text = getString(R.string.pub)}}
}