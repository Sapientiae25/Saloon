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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
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
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.abs

class SettingFragment : Fragment(){

    private lateinit var tvAddress: TextView
    private lateinit var tvDetails: TextView
    private lateinit var tvOpen: TextView
    private lateinit var tvClose: TextView
    private lateinit var tvPayment: TextView
    private lateinit var tvAddImage: TextView
    private lateinit var tvPassword: TextView
    private lateinit var accountItem: AccountItem
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private lateinit var startGalleryForResult: ActivityResultLauncher<Intent>
    private lateinit var vpImages: ViewPager2
    private val imageUrls = mutableListOf("")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_setting, container, false)
        (activity as DefaultActivity).supportActionBar?.title = "Settings"
        accountItem = (activity as DefaultActivity).accountItem
        tvDetails = rootView.findViewById(R.id.tvDetails)
        tvAddress = rootView.findViewById(R.id.tvAddress)
        tvOpen = rootView.findViewById(R.id.tvOpen)
        tvClose = rootView.findViewById(R.id.tvClose)
        tvPayment = rootView.findViewById(R.id.tvPayment)
        tvPassword = rootView.findViewById(R.id.tvPassword)
        tvAddImage = rootView.findViewById(R.id.tvAddImage)
        tvAddress.setOnClickListener { view -> view.findNavController().navigate(R.id.action_settingFragment_to_locationFragment)}
        tvDetails.setOnClickListener { view -> view.findNavController().navigate(R.id.action_settingFragment_to_saloonDetailsFragment)}
        tvOpen.setOnClickListener {showCustomDialog(tvOpen,true) }
        tvClose.setOnClickListener {showCustomDialog(tvClose,false) }
        tvPassword.setOnClickListener {view -> view.findNavController().navigate(R.id.action_settingFragment_to_passwordFragment)}
        tvPayment.setOnClickListener { view -> view.findNavController().navigate(R.id.action_settingFragment_to_paymentMethodFragment) }

        vpImages = rootView.findViewById(R.id.vpImages)
        val sliderHandler = Handler(Looper.getMainLooper())
        val tabLayout = rootView.findViewById<TabLayout>(R.id.tabLayout)
        val adapter = SaloonImageAdapter(imageUrls) {index -> addImage(index) }
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

        val url = getString(R.string.url,"open_times.php")
        val stringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                println(response)
                val obj = JSONObject(response)
                val open = obj.getString("open")
                val close = obj.getString("close")
                tvOpen.text = getString(R.string.open_,open)
                tvClose.text = getString(R.string.close_,close) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["account_id"] = accountItem.id
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

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
        tvAddImage.setOnClickListener { photoOption() }
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
        val url = getString(R.string.url,"delete_saloon_image.php")
        val stringRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->
            Toast.makeText(context,response, Toast.LENGTH_SHORT).show()},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
        override fun getParams(): Map<String, String> {
            val params = java.util.HashMap<String, String>()
            params["url"] = imageId
            return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest) }
    private fun loadImages(){
        val url = getString(R.string.url,"get_saloon_images.php")
        val stringRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->
            val arr = JSONArray(response)
            for (i in 0 until arr.length()){
                val imageId = arr.getString(i)
                imageUrls.add(imageId)}
            vpImages.adapter?.notifyItemRangeInserted(1,imageUrls.size)},
            Response.ErrorListener { volleyError -> println(volleyError.message) }) { @Throws(AuthFailureError::class)
        override fun getParams(): Map<String, String> {
            val params = java.util.HashMap<String, String>()
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
                val params = java.util.HashMap<String, String>()
                params["image"] = image
                params["account_id"] = accountItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest) }
    private fun showCustomDialog(textView: TextView,open: Boolean) {
        val dialog = Dialog(requireContext())
        var hour = 0
        var minute = 0
        val minOptions = arrayOf("00","15","30","45")
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.time_picker_layout)
        val numPickerHour = dialog.findViewById<NumberPicker>(R.id.numPickerHour)
        val numPickerMins = dialog.findViewById<NumberPicker>(R.id.numPickerMins)
        val save = dialog.findViewById<TextView>(R.id.save)
        val close = dialog.findViewById<TextView>(R.id.close)

        numPickerHour.minValue = 0
        numPickerHour.maxValue  = 23
        numPickerMins.minValue = 0
        numPickerMins.maxValue = 3
        numPickerMins.displayedValues = minOptions
        numPickerHour.setOnValueChangedListener { numberPicker, _, _ ->  hour = numberPicker.value}
        numPickerMins.setOnValueChangedListener { numberPicker, _, _ ->
            val x = minOptions[numberPicker.value]
            minute = x.toInt() }
        close.setOnClickListener { dialog.dismiss() }
        save.setOnClickListener {val timeText = getString(R.string.clock,hour,minute);textView.text = timeText
            if (open){
                val url = getString(R.string.url,"open.php")
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener {},
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["account_id"] = accountItem.id
                        params["time"] = timeText
                        return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
                tvOpen.text = getString(R.string.open_,timeText)
            }else{
                val url = getString(R.string.url,"close.php")
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener {},
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["account_id"] = accountItem.id
                        params["time"] = timeText
                        return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
                tvClose.text = getString(R.string.close_,timeText) };dialog.dismiss()
        }
        dialog.show() }
}