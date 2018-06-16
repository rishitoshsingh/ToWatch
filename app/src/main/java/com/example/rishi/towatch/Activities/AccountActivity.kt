package com.example.rishi.towatch.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import com.example.rishi.towatch.Adapters.AccountAdapter
import com.example.rishi.towatch.R
import com.example.rishi.towatch.firebase.SignUpActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_account.*


class AccountActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private lateinit var mSharedPreferences:SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        mAuth = FirebaseAuth.getInstance()

        if (mAuth?.currentUser == null) {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            if(mAuth?.currentUser == null){
                finish()
            }
        }

        setSupportActionBar(account_toolbar)
        val toolbar = supportActionBar
        toolbar?.title = "Account"

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        toolbar?.title = "Welcome, " + mSharedPreferences.getString("GivenName","No One")

        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        accountViewPager.adapter = AccountAdapter(this, supportFragmentManager)
        account_tab_Layout.setupWithViewPager(accountViewPager)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.account_actionbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.sign_out -> {
                FirebaseAuth.getInstance().signOut()
                finish()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)

    }
}
