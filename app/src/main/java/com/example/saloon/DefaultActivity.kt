package com.example.saloon

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class DefaultActivity : AppCompatActivity() {

    lateinit var accountItem: AccountItem
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)
        accountItem = intent.getParcelableExtra("account_item")!!
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.activityFragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId){
                R.id.saloonFragment -> it.onNavDestinationSelected(navController)
                R.id.calendarFragment -> it.onNavDestinationSelected(navController)
            }
            true }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true }

    override fun onSupportNavigateUp(): Boolean { return navController.navigateUp() || super.onSupportNavigateUp() }
}