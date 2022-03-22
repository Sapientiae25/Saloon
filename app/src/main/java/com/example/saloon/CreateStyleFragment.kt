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
import kotlin.math.abs

class CreateStyleFragment : Fragment() {

    private lateinit var etDuration: AutoCompleteTextView
    private var minute = 0
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private lateinit var startGalleryForResult: ActivityResultLauncher<Intent>
    private lateinit var vpImages: ViewPager2
    private lateinit var imageList: ArrayList<Bitmap>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_create_style, container, false)
        val etName = rootView.findViewById<TextInputEditText>(R.id.etName)
        val etPrice = rootView.findViewById<TextInputEditText>(R.id.etPrice)
        val tvAddImage = rootView.findViewById<TextView>(R.id.tvAddImage)
        val btnCreateStyle = rootView.findViewById<Button>(R.id.btnCreateStyle)
        val etInfo = rootView.findViewById<TextInputEditText>(R.id.etInfo)
        etDuration = rootView.findViewById(R.id.etDuration)
        val tvGender = rootView.findViewById<TextView>(R.id.tvGender)
        val tvLength = rootView.findViewById<TextView>(R.id.tvLength)
        val rgGender = rootView.findViewById<RadioGroup>(R.id.rgGender)
        val rgLength = rootView.findViewById<RadioGroup>(R.id.rgLength)
        vpImages = rootView.findViewById(R.id.vpImages)
        val tabLayout = rootView.findViewById<TabLayout>(R.id.tabLayout)
        val sliderHandler = Handler(Looper.getMainLooper())
        imageList = arrayListOf()

        etDuration.setOnClickListener { showCustomDialog() }

        val adapter = CreateStyleImageAdapter(imageList) {index -> addImage(index) }
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
        val sliderRunnable = Runnable {vpImages.currentItem = if(vpImages.currentItem+1 == imageList.size) 0
        else vpImages.currentItem+1}

        TabLayoutMediator(tabLayout,vpImages) { _, _ -> }.attach()

        vpImages.registerOnPageChangeCallback( object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 2000) } })

        btnCreateStyle.setOnClickListener { view ->
            var filled = true
            if (etName.text!!.isEmpty()){filled=false;etName.error="This field must be filled"}
            if (etPrice.text!!.isEmpty()){filled=false;etPrice.error="This field must be filled"}
            if (minute == 0){filled=false;etDuration.error="This field must be filled"}
            if (filled){
                val url = getString(R.string.url,"style_check.php")
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        if (response == "1"){
                            Toast.makeText(context, "Style Already Exists",Toast.LENGTH_SHORT).show() }
                        else{
                            val genderId: Int = rgGender.checkedRadioButtonId
                            val genderButton: View = rgGender.findViewById(genderId)
                            var gender = rgGender.indexOfChild(genderButton)
                            val lengthId: Int = rgLength.checkedRadioButtonId
                            val lengthButton: View = rgLength.findViewById(lengthId)
                            var length = rgLength.indexOfChild(lengthButton)
                            if (length == 0) {length = rgLength.childCount-1} else if (length == rgLength.childCount-1){length = 0}
                            if (gender == 0) {gender = rgGender.childCount-1} else if (gender ==  rgGender.childCount-1){gender = 0}
                            val filterItem = StyleFilterItem(gender,length)
                            val timeItem = TimeItem(minute.toString())
                            val styleItem = StyleItem(etName.text.toString(),etPrice.text.toString().toFloat(),timeItem,
                                etInfo.text.toString(),filterItem=filterItem)
                            val bundle = bundleOf(Pair("styleItem",styleItem),Pair("imageList",imageList))
                            view.findNavController().navigate(R.id.action_createStyleFragment_to_chooseCategoryFragment,bundle)
                        } },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["name"] = etName.text.toString()
                        return params
                    }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest) }
        }

        tvLength.setOnClickListener { rgLength.visibility = if (rgLength.visibility == View.GONE) View.VISIBLE else View.GONE }
        tvGender.setOnClickListener { rgGender.visibility = if (rgGender.visibility == View.GONE) View.VISIBLE else View.GONE }
        tvAddImage.setOnClickListener {photoOption()}

        startGalleryForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val takenImage = intent?.extras?.get("data") as Bitmap
                imageList.add(takenImage)
                vpImages.adapter?.notifyItemInserted(imageList.size)
            } }
        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val takenImage = intent?.extras?.get("data") as Bitmap
                imageList.add(takenImage)
                vpImages.adapter?.notifyItemInserted(imageList.size)
            } }
        return rootView
    }

    private fun addImage(index: Int) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.add_image_layout)
        val remove = dialog.findViewById<TextView>(R.id.remove)
        val add = dialog.findViewById<TextView>(R.id.add)
        if (index == 0){if (imageList.size == 5) dialog.dismiss(); remove.visibility = View.GONE}
        else if (imageList.size == 5)  add.visibility = View.GONE
        remove.setOnClickListener { imageList.removeAt(index);vpImages.adapter?.notifyItemRemoved(index); dialog.dismiss() }
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
        numPicker.setOnValueChangedListener { numberPicker, _, _ ->
            val x = minOptions[numberPicker.value]; minute = x.toInt()}
        close.setOnClickListener { dialog.dismiss() }
        save.setOnClickListener {dialog.dismiss(); etDuration.setText(getString(R.string.time_mins,minute.toString())) }
        dialog.show() }
}