package com.example.saloon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject
import java.util.*

class EditStyleFragment : Fragment(){

    private lateinit var accountItem : AccountItem
    private lateinit var styleItem : StyleItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getParcelable<AccountItem>("accountItem")?.let {
            accountItem = it
        }
        arguments?.getParcelable<StyleItem>("styleItem")?.let {
            styleItem = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_edit_style, container, false)

        val etName = rootView.findViewById<TextInputEditText>(R.id.etName)
        val etPrice = rootView.findViewById<TextInputEditText>(R.id.etPrice)
        val etDuration = rootView.findViewById<TextInputEditText>(R.id.etDuration)
        val etMaxTime = rootView.findViewById<TextInputEditText>(R.id.etMaxTime)
        val etTags = rootView.findViewById<TextInputEditText>(R.id.etTags)
        val tlTags = rootView.findViewById<TextInputLayout>(R.id.tlTags)
        val tvAddImage = rootView.findViewById<TextView>(R.id.tvAddImage)
        val ivStyleImage = rootView.findViewById<ImageView>(R.id.ivStyleImage)
        val btnCreateStyle = rootView.findViewById<Button>(R.id.btnCreateStyle)
        val cgTags = rootView.findViewById<ChipGroup>(R.id.cgTags)
        val etInfo = rootView.findViewById<TextInputEditText>(R.id.etInfo)
        val tagList = mutableListOf<String>()

        etName.setText(styleItem.name)
        etPrice.setText(styleItem.price.toString())
        val timeItem = styleItem.time
        etDuration.setText(timeItem.time)
        if(timeItem.maxTime != null){etMaxTime.setText(timeItem.maxTime)}
        etInfo.setText(styleItem.info)
        for (tag in styleItem.tags){
            tagList.add(tag)
            val chip = Chip(context)
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

        tlTags.setEndIconOnClickListener{Toast.makeText(context,"Words our users can use to find you",Toast.LENGTH_SHORT).show()}
        etTags.setOnKeyListener{ _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP && tagList.size > 5) {
                val tagText = etTags.text.toString()
                tagList.add(tagText)
                val chip = Chip(context)
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
                            Toast.makeText(context, "Style already exists",Toast.LENGTH_SHORT).show()
                        }else{
                            val intent = Intent(context, UserActivity::class.java)
                            intent.putExtra("account_id", accountItem.id)
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
                        params["account_id"] = accountItem.id
                        params["info"] = etInfo.text.toString()
                        return params
                    }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
            }
        }
        return rootView
    }

    companion object {
        fun newInstance(param1: AccountItem,param2 : StyleItem) =
            SaloonNameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("accountItem", param1)
                    putParcelable("styleItem", param2)
                }
            }
    }
}