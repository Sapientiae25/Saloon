package com.example.saloon

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject

class DefaultActivity : AppCompatActivity() {

    lateinit var accountItem: AccountItem
    private lateinit var navController: NavController
    private var privacy = true
    var hasPayment = true
    private var notificationCount = 0
    private lateinit var switchPrivacy: SwitchCompat
    private lateinit var notification: ConstraintLayout
    private lateinit var cvCount: CardView
    private lateinit var tvCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)
        accountItem = intent.getParcelableExtra("account_item")!!
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        notification = findViewById(R.id.notification)
        cvCount = findViewById(R.id.cvCount)
        tvCount = findViewById(R.id.tvCount)
        switchPrivacy = findViewById(R.id.switchPrivacy)
        setSupportActionBar(toolbar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.activityFragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
        val url = getString(R.string.url,"check_privacy.php")
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                Log.println(Log.ASSERT,"priv",response)
                val obj = JSONObject(response)
                privacy = obj.getInt("privacy") == 1
                hasPayment = obj.getString("payment").toIntOrNull() != null
                if (privacy){switchPrivacy.isChecked = !privacy;switchPrivacy.text = getString(R.string.priv)}
                else {if (!hasPayment){showCustomDialog()}else{switchPrivacy.isChecked=!privacy;switchPrivacy.text=getString(R.string.pub)}}
            },
            Response.ErrorListener { volleyError -> println(volleyError.message) }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["account_id"] = accountItem.id
                return params }}
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        switchPrivacy.setOnClickListener {changePrivacy() }
        notification.setOnClickListener { findNavController(R.id.activityFragment).navigate(R.id.action_global_bookingFragment) }
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId){
                R.id.saloonFragment -> it.onNavDestinationSelected(navController)
                R.id.calendarFragment -> it.onNavDestinationSelected(navController) }
            true } }
    override fun onSupportNavigateUp(): Boolean { return navController.navigateUp() || super.onSupportNavigateUp() }
    private fun changePrivacy(){if (!privacy){privacy=true;switchPrivacy.isChecked = !privacy; switchPrivacy.text = getString(R.string.priv)}
    else { if (!hasPayment){showCustomDialog();switchPrivacy.isChecked=false}
    else {privacy=false;switchPrivacy.isChecked=!privacy;switchPrivacy.text = getString(R.string.pub)}}}
    private fun showCustomDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.payment_pop_up)
        val save = dialog.findViewById<TextView>(R.id.save)
        val close = dialog.findViewById<TextView>(R.id.close)

        close.setOnClickListener { dialog.dismiss() }
        save.setOnClickListener { view -> view.findNavController().navigate(R.id.action_global_paymentMethodFragment) ;dialog.dismiss() }
        dialog.show() }
    fun addNotification(){ if (notificationCount < 100){
        notificationCount += 1; cvCount.visibility = View.VISIBLE; tvCount.text = notificationCount.toString() } }
    fun clearNotification(){ cvCount.visibility = View.GONE; notificationCount = 0 }
}