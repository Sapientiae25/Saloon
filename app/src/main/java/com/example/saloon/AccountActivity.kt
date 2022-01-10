package com.example.saloon

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit

class AccountActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        val accountItem = intent.getParcelableExtra<AccountItem>("account_item")
        supportFragmentManager.commit { add(R.id.fragmentContainer,UpdateFragment.initAccount(accountItem!!))
        }}


}