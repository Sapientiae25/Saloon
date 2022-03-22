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
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
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
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.abs

class EditStyleFragment : Fragment(){

    private lateinit var styleItem : StyleItem
    private lateinit var etDuration: AutoCompleteTextView
    private var minute = 0
    private lateinit var vpImages: ViewPager2
    private val imageUrls = mutableListOf("")
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private lateinit var startGalleryForResult: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_edit_style, container, false)
        val accountItem = (activity as DefaultActivity).accountItem
        styleItem = arguments?.getParcelable("styleItem") !!
        (activity as DefaultActivity).supportActionBar?.title = styleItem.name
        val tvUserView = rootView.findViewById<TextView>(R.id.tvUserView)
        val etName = rootView.findViewById<TextInputEditText>(R.id.etName)
        val etPrice = rootView.findViewById<TextInputEditText>(R.id.etPrice)
        val tvAddImage = rootView.findViewById<TextView>(R.id.tvAddImage)
        val btnCreateStyle = rootView.findViewById<Button>(R.id.btnCreateStyle)
        etDuration = rootView.findViewById(R.id.etDuration)
        val etInfo = rootView.findViewById<TextInputEditText>(R.id.etInfo)
        val tvGender = rootView.findViewById<TextView>(R.id.tvGender)
        val tvLength = rootView.findViewById<TextView>(R.id.tvLength)
        val rgGender = rootView.findViewById<RadioGroup>(R.id.rgGender)
        val rgLength = rootView.findViewById<RadioGroup>(R.id.rgLength)

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

        etName.setText(styleItem.name)
        etPrice.setText(styleItem.price.toString())
        etInfo.setText(styleItem.info)
        val timeItem = styleItem.time
        etDuration.setText(timeItem.time)
        btnCreateStyle.setOnClickListener {view ->
            var filled = true
            if (etName.text!!.isEmpty()){filled=false;etName.error="This field must be filled"}
            if (etPrice.text!!.isEmpty()){filled=false;etPrice.error="This field must be filled"}
            if (minute == 0){filled=false;etDuration.error="This field must be filled"}
            if (etInfo.text!!.isEmpty()){filled=false;etInfo.error="This field must be filled"}
            val genderId: Int = rgGender.checkedRadioButtonId
            val genderButton: View = rgGender.findViewById(genderId)
            var gender = rgGender.indexOfChild(genderButton)
            val lengthId: Int = rgLength.checkedRadioButtonId
            val lengthButton: View = rgLength.findViewById(lengthId)
            var length = rgLength.indexOfChild(lengthButton)
            if (length == 0) {length = rgLength.childCount-1} else if (length == rgLength.childCount-1){length = 0}
            if (gender == 0) {gender = rgGender.childCount-1} else if (gender ==  rgGender.childCount-1){gender = 0}
            if (filled){
                styleItem.name = etName.text.toString()
                styleItem.price = etPrice.text.toString().toFloat()
                styleItem.time = TimeItem(minute.toString())
                styleItem.info = etInfo.text.toString()

                val url = getString(R.string.url,"update_style.php")
                val stringRequest: StringRequest = object : StringRequest(
                    Method.GET, url, Response.Listener { response ->
                        val obj = JSONObject(response)
                        val exist = obj.getInt("exist")
                        if (exist == 1){
                            Toast.makeText(context, "Style already exists",Toast.LENGTH_SHORT).show()
                        }else{val bundle = bundleOf(Pair("styleItem",styleItem))
                            view.findNavController().navigate(R.id.action_editStyleFragment_to_styleFragment,bundle) } },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["name"] = styleItem.name
                        params["price"] = styleItem.price.toString()
                        params["time"] = styleItem.time.time
                        params["account_id"] = accountItem.id
                        params["info"] = styleItem.info
                        params["style_id"] = styleItem.id
                        params["gender"] = gender.toString()
                        params["length"] = length.toString()
                        return params
                    }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
            }
        }
        etDuration.setOnClickListener { showCustomDialog() }
        tvLength.setOnClickListener { rgLength.visibility = if (rgLength.visibility == View.GONE) View.VISIBLE else View.GONE }
        tvGender.setOnClickListener { rgGender.visibility = if (rgGender.visibility == View.GONE) View.VISIBLE else View.GONE }
        val url = getString(R.string.url,"get_style_filters.php")
        val stringRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->
            Log.println(Log.ASSERT,"FIL",response)
            val obj = JSONObject(response)
            var length = obj.getInt("length")
            var gender = obj.getInt("gender")
            if (length == 0) {length = rgLength.childCount-1} else if (length == rgLength.childCount-1){length = 0}
            if (gender == 0) {gender = rgGender.childCount-1} else if (gender ==  rgGender.childCount-1){gender = 0}
            (rgLength.getChildAt(length) as RadioButton).isChecked = true
            (rgGender.getChildAt(gender) as RadioButton).isChecked=true},
            Response.ErrorListener{volleyError->println(volleyError.message)}){@Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["style_id"] = styleItem.id
                return params
            }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        tvUserView.setOnClickListener{view ->
            val bundle = bundleOf(Pair("styleItem",styleItem))
            view.findNavController().navigate(R.id.action_styleFragment_to_editStyleFragment,bundle) }

        tvAddImage.setOnClickListener { photoOption() }
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
    private fun showCustomDialog() {
        val dialog = Dialog(requireContext())
        val minOptions = mutableListOf<String>()
        for (i in 15 until 315 step(15)){minOptions.add(i.toString()) }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.number_picker_layout)
        val numPicker = dialog.findViewById<NumberPicker>(R.id.numPicker)
        val save = dialog.findViewById<TextView>(R.id.save)
        val close = dialog.findViewById<TextView>(R.id.close)

        numPicker.minValue = 0
        numPicker.maxValue = 19
        numPicker.displayedValues = minOptions.toTypedArray()
        numPicker.setOnValueChangedListener { numberPicker, _, _ -> val x = minOptions[numberPicker.value]; minute = x.toInt()}
        close.setOnClickListener { dialog.dismiss() }
        save.setOnClickListener {dialog.dismiss(); etDuration.setText(minute.toString()) }
        dialog.show()

}}