package com.example.saloon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import org.json.JSONArray

class StyleFragment : Fragment() {

    private lateinit var accountItem : AccountItem
    private lateinit var styleItem : StyleItem
    private lateinit var timeItem : TimeItem
    private lateinit var timeValue: String
    private var privacy = true
    private lateinit var switchPrivacy: SwitchCompat

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
        val ivStyleImage = rootView.findViewById<ImageSlider>(R.id.ivStyleImage)
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.trim, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.trim, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.trim, ScaleTypes.FIT))
        ivStyleImage.setImageList(imageList)
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
        getString(R.string.url,"saloon_get_style.php")
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
                    similarStyles.add(StyleItem(name,price,timeItem,info,styleId,rating=rating)) }
                rvMoreLike.adapter?.notifyItemRangeInserted(0,similarStyles.size) },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["account_id"] = accountItem.id
                return params
            }}
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
        return rootView
    }
    private fun changePrivacy(){if (privacy){switchPrivacy.isChecked = !privacy; switchPrivacy.text = getString(R.string.priv)}
    else {switchPrivacy.isChecked = !privacy; switchPrivacy.text = getString(R.string.pub)}}
}