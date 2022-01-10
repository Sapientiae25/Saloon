package com.example.saloon

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.view.KeyEvent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.createBalloon
import org.json.JSONObject
import java.util.*

class CreateStyleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_style)

        val etName = findViewById<TextInputEditText>(R.id.etName)
        val etPrice = findViewById<TextInputEditText>(R.id.etPrice)
        val etDuration = findViewById<TextInputEditText>(R.id.etDuration)
        val etMaxTime = findViewById<TextInputEditText>(R.id.etMaxTime)
        val etTags = findViewById<TextInputEditText>(R.id.etTags)
        val tlTags = findViewById<TextInputLayout>(R.id.tlTags)
        val tvAddImage = findViewById<TextView>(R.id.tvAddImage)
        val ivStyleImage = findViewById<ImageView>(R.id.ivStyleImage)
        val btnCreateStyle = findViewById<Button>(R.id.btnCreateStyle)
        val cgTags = findViewById<ChipGroup>(R.id.cgTags)
        val etInfo = findViewById<TextInputEditText>(R.id.etInfo)
        val accountItem = intent.getParcelableExtra<AccountItem>("account_item")
        val tagList = mutableListOf<String>()
        val balloon = createBalloon(this) {
            setArrowSize(10)
            setWidthRatio(1.0f)
            setHeight(65)
            setArrowPosition(0.7f)
            setCornerRadius(4f)
            setAlpha(0.9f)
            setText("Words our users can use to find you")
            setTextColorResource(R.color.white)
            setBackgroundColorResource(R.color.teal_200)
            setBalloonAnimation(BalloonAnimation.FADE)
            setLifecycleOwner(lifecycleOwner)
            dismissWhenClicked = true
            dismissWhenTouchOutside = true
        }

        tlTags.setEndIconOnClickListener{balloon.showAlignTop(tlTags)
                balloon.dismissWithDelay(3000L)}
        etTags.setOnKeyListener{ _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP && tagList.size < 5) {
                val tagText = etTags.text.toString()
                tagList.add(tagText)
                val chip = Chip(this)
                chip.text = tagText
                chip.isClickable = true
                chip.isCloseIconVisible = true
                cgTags.addView(chip)
                chip.setOnCloseIconClickListener{
                    TransitionManager.beginDelayedTransition(cgTags)
                    tagList.remove(chip.text)
                    cgTags.removeView(chip)
                }
                etTags.text?.clear()
            }
            false
        }
        fun fill(){
            etName.setText("Braids")
            etPrice.setText("10")
            etDuration.setText("30")
            etMaxTime.setText("60")
            etInfo.setText("Beautiful braids!")
            val tagWords = arrayListOf("braids","hair","black")
            for (tag in tagWords){
                tagList.add(tag)
                val chip = Chip(this)
                chip.text = tag
                chip.isClickable = true
                chip.isCloseIconVisible = true
                cgTags.addView(chip)
                chip.setOnCloseIconClickListener{
                    TransitionManager.beginDelayedTransition(cgTags)
                    tagList.remove(chip.text)
                    cgTags.removeView(chip)
                }
            }}
        fill()

        btnCreateStyle.setOnClickListener {
            var filled = true
            if (etName.text!!.isEmpty()){filled=false;etName.error="This field must be filled"}
            if (etPrice.text!!.isEmpty()){filled=false;etPrice.error="This field must be filled"}
            if (etDuration.text!!.isEmpty()){filled=false;etDuration.error="This field must be filled"}
            val maxTime = if (etMaxTime.text!!.isEmpty()){null}else{etMaxTime.text}
            if (filled){
                val url = "http://192.168.1.102:8012/saloon/create_style.php"
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        println("r $response")
                        val obj = JSONObject(response)
                        val exist = obj.getInt("exist")
                        if (exist == 1){
                            Toast.makeText(this, "Style already exists",Toast.LENGTH_SHORT).show()
                        }else{
                            val styleId = obj.getString("style_id")
                            val url3 = "http://192.168.1.102:8012/saloon/delete_tag.php"
                            val stringRequest3 = object : StringRequest(
                                Method.POST, url3, Response.Listener {},
                                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                                @Throws(AuthFailureError::class)
                                override fun getParams(): Map<String, String> {
                                    val params = HashMap<String, String>()
                                    params["style_id"] = styleId
                                    return params
                                }}
                            VolleySingleton.instance?.addToRequestQueue(stringRequest3)
                            for (tag in tagList){
                                val url2 = "http://192.168.1.102:8012/saloon/create_tag.php"
                                val stringRequest2 = object : StringRequest(
                                    Method.POST, url2, Response.Listener {},
                                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                                    @Throws(AuthFailureError::class)
                                    override fun getParams(): Map<String, String> {
                                        val params = HashMap<String, String>()
                                        params["tag"] = tag
                                        params["style_id"] = styleId
                                        return params
                                    }}
                                VolleySingleton.instance?.addToRequestQueue(stringRequest2)
                            }
                            val intent = Intent(this, UserActivity::class.java)
                            intent.putExtra("account_item", accountItem)
                            startActivity(intent)
                        }
                    },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["name"] = etName.text.toString()
                        params["price"] = etPrice.text.toString()
                        params["time"] = etDuration.text.toString()
                        params["max_time"] = maxTime.toString()
                        params["account_id"] = accountItem!!.id
                        params["info"] = etInfo.text.toString()
                        return params
                    }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
            }
        }
    }
}