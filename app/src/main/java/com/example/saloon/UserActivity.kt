package com.example.saloon

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import org.json.JSONArray
import java.util.*

class UserActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        val accountItem = intent.getParcelableExtra<AccountItem>("account_item")
        val styleItemList = mutableListOf<StyleItem>()
        val rvStyleItems = findViewById<RecyclerView>(R.id.rvStyleItems)
        rvStyleItems.adapter = StyleItemAdapter(styleItemList,accountItem!!)
        rvStyleItems.layoutManager = LinearLayoutManager(this)
        rvStyleItems.setHasFixedSize(true)
        val navView: NavigationView = findViewById(R.id.navView)
        val btnNewStyle = findViewById<FloatingActionButton>(R.id.btnNewStyle)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val actionBar = supportActionBar
        val tvNoStyles = findViewById<TextView>(R.id.tvNoStyles)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar!!.title = accountItem.name
        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.opens,R.string.closes)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnNewStyle.setOnClickListener {
            val intent = Intent(this, CreateStyleActivity::class.java)
            intent.putExtra("account_item", accountItem)
            startActivity(intent)
        }

        val url = "http://192.168.1.102:8012/saloon/get_style.php"
        val stringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { response ->
                println(response)
                val arr = JSONArray(response)
                if (arr.length() == 0){tvNoStyles.visibility = View.VISIBLE}
                for (x in 0 until arr.length()){
                    val obj = arr.getJSONObject(x)
                    val tags = mutableListOf<String>()
                    val name = obj.getString("name")
                    val price = obj.getString("price").toFloat()
                    val time = obj.getString("time")
                    val styleId = obj.getString("style_id")
                    val maxTime = obj.getString("max_time")
                    val info = obj.getString("info")
                    val tagArray = obj.getJSONArray("tags")
                    for (y in 0 until tagArray.length()){
                        val tag = tagArray.getString(y); tags.add(tag)}
                    val timeItem = TimeItem(time,maxTime)
                    styleItemList.add(StyleItem(name,price,timeItem,info,tags,styleId)) }
                rvStyleItems.adapter?.notifyItemRangeInserted(0,styleItemList.size) },
                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["account_id"] = accountItem.id
                    return params
                }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miAccount -> {
                    val intent = Intent(this, AccountActivity::class.java)
                    intent.putExtra("account_item",accountItem)
                    startActivity(intent)
                }};true }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected((item))){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}