package com.alphae.rishi.towatch.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import com.alphae.rishi.towatch.Adapters.AccountAdapter
import com.alphae.rishi.towatch.Database.WatchDatabase
import com.alphae.rishi.towatch.Database.WatchList
import com.alphae.rishi.towatch.Database.WatchedList
import com.alphae.rishi.towatch.R
import com.alphae.rishi.towatch.firebase.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_account.*


class AccountActivity : AppCompatActivity() {

    private var watchList: MutableList<WatchList> = mutableListOf<WatchList>()
    private var watchedList: MutableList<WatchedList> = mutableListOf<WatchedList>()
    private lateinit var watchDatabase: WatchDatabase
    private lateinit var firebaseDatabase:FirebaseDatabase
    private var mAuth: FirebaseAuth? = null
    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        mAuth = FirebaseAuth.getInstance()


        if (mAuth?.currentUser == null) {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            if (mAuth?.currentUser == null) {
                finish()
            }
        }

        setSupportActionBar(account_toolbar)
        val toolbar = supportActionBar
        toolbar?.title = "Account"

        mSharedPreferences = getSharedPreferences("Account", Context.MODE_PRIVATE)
        toolbar?.title = "Welcome, " + mSharedPreferences.getString("GivenName", "No One")

        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.navigationBarColor = resources.getColor(R.color.primary)
        window.statusBarColor = resources.getColor(R.color.md_black_1000)

        accountViewPager.adapter = AccountAdapter(this, supportFragmentManager)
        account_tab_Layout.setupWithViewPager(accountViewPager)

        firebaseDatabase = FirebaseDatabase.getInstance()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.account_actionbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {

            R.id.cloud_save -> {
                watchList.removeAll(watchList)
                watchedList.removeAll(watchedList)
                watchDatabase = WatchDatabase.getInstance(this)!!
                ReadFromDatabase().execute()
                ReadFromDatabaseWatched().execute()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)

    }

    private inner class ReadFromDatabase : AsyncTask<Void, Void, List<WatchList>>() {
        override fun doInBackground(vararg params: Void?): List<WatchList> {
            return watchDatabase.watchDaoAccess().fetchAllMovies()
        }

        override fun onPostExecute(result: List<WatchList>?) {
            for (item in result!!) watchList.add(item)
            Log.d("Error", watchList.size.toString())
            val firebasePlaylists = firebaseDatabase.getReference("playlist/" + mAuth?.currentUser?.uid)
            firebasePlaylists.setValue(watchList)
        }
    }

    private inner class ReadFromDatabaseWatched : AsyncTask<Void, Void, List<WatchedList>>() {
        override fun doInBackground(vararg params: Void?): List<WatchedList> {
            return watchDatabase.watchedDaoAccess().fetchAllMovies()
        }

        override fun onPostExecute(result: List<WatchedList>?) {
            for (item in result!!) watchedList.add(item)
            Log.d("Error", watchedList.size.toString())
            val firebaseWatched = firebaseDatabase.getReference("watched/" + mAuth?.currentUser?.uid)
            firebaseWatched.setValue(watchedList)
        }
    }

    override fun onDestroy() {
        WatchDatabase.destroyInstance()
        super.onDestroy()
    }
}
