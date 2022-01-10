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

class UpdateStyleActivity : AppCompatActivity() {
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
        val edit = intent.getBooleanExtra("edit",false)
        val accountItem = intent.getParcelableExtra<AccountItem>("account_item")
        val styleItem = intent.getParcelableExtra<StyleItem>("style_item")
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

        etName.setText(styleItem!!.name)
        etPrice.setText(styleItem.price.toString())
        val timeItem = styleItem.time
        etDuration.setText(timeItem.time)
        if(timeItem.maxTime != null){etMaxTime.setText(timeItem.maxTime)}
        etInfo.setText(styleItem.info)
        for (tag in styleItem.tags){
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
        }

        tlTags.setEndIconOnClickListener{balloon.showAlignTop(tlTags)
            balloon.dismissWithDelay(3000L)}
        etTags.setOnKeyListener{ _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP && tagList.size > 5) {
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

        btnCreateStyle.setOnClickListener {
            var filled = true
            if (etName.text!!.isEmpty()){filled=false;etName.error="This field must be filled"}
            if (etPrice.text!!.isEmpty()){filled=false;etPrice.error="This field must be filled"}
            if (etDuration.text!!.isEmpty()){filled=false;etDuration.error="This field must be filled"}
            val maxTime = if (etMaxTime.text!!.isEmpty()){null}else{etMaxTime.text}
            if (filled){
                val url = "http://192.168.1.102:8012/saloon/create_style.php"
                val stringRequest = object : StringRequest(
                    Method.GET, url, Response.Listener { response ->
                        val obj = JSONObject(response)
                        val exist = obj.getInt("exist")
                        if (exist == 1){
                            Toast.makeText(this, "Style already exists",Toast.LENGTH_SHORT).show()
                        }else{
                            val intent = Intent(this, UserActivity::class.java)
                            intent.putExtra("account_id", accountItem!!.id)
                            intent.putExtra("name", accountItem.name)
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

        if (edit){btnCreateStyle.text = getString(R.string.update_style)}
    }
}