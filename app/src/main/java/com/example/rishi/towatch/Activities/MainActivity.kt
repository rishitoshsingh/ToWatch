package com.example.rishi.towatch.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
import android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
import com.example.rishi.towatch.Adapters.HomeAdapter
import com.example.rishi.towatch.Api.Clients.YouTubeClient
import com.example.rishi.towatch.Api.ServiceGenerator
import com.example.rishi.towatch.BuildConfig
import com.example.rishi.towatch.Fragments.SearchFragment
import com.example.rishi.towatch.POJOs.YouTube.YouTubeVideo
import com.example.rishi.towatch.R
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private var actionSearchView: android.support.v7.widget.SearchView? = null
    private lateinit var searchMenuItem:MenuItem
    private lateinit var mSharedPreferences: SharedPreferences



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(my_toolbar)
        val toolbar = supportActionBar

        val window: Window = window
        window.clearFlags(FLAG_TRANSLUCENT_STATUS)
        window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        val extras = intent.extras
        if (extras != null) {
            try {
                val value1 = extras.getString(Intent.EXTRA_TEXT)
                var videoId = value1.substringAfter("https://youtu.be/")
                val ytClient = ServiceGenerator.createYtService(YouTubeClient::class.java)
                val call = ytClient.getVideoTitle(videoId, BuildConfig.YoutubeApiKey)
                call.enqueue(object : Callback<YouTubeVideo> {
                    override fun onFailure(call: Call<YouTubeVideo>?, t: Throwable?) {

                    }

                    override fun onResponse(call: Call<YouTubeVideo>?, response: Response<YouTubeVideo>?) {
                        val videoTitle: String = response?.body()?.items!![0].snippet.title
                        searchMenuItem.expandActionView()
                        actionSearchView?.setQuery(modifyTitle(videoTitle),true)
                    }

                    private fun modifyTitle(title: String): String {
//                    val temp1 = title.split("official",true,0)[0]
                        var temp1 = title.replace("official","*",true)
                        temp1 = temp1.replace("trailer","*",true)
                        temp1 = temp1.replace("|","*",true)
                        temp1 = temp1.replace("#","*",true)
                        return temp1.split("*")[0].trim()
                    }
                })
            } catch ( ex:Exception) {

            }

        }


        viewPager.adapter = HomeAdapter(this, supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.actionbar_menu, menu)

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val searchItem = menu?.findItem(R.id.app_bar_search)
        searchMenuItem = searchItem!!
        actionSearchView = searchItem.actionView as android.support.v7.widget.SearchView

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                menu.findItem(R.id.app_bar_profile).isVisible = false
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                menu.findItem(R.id.app_bar_profile).isVisible = true
                val fragment = supportFragmentManager.findFragmentById(R.id.mainView)
                if (fragment != null) {
                    supportFragmentManager.beginTransaction().remove(supportFragmentManager.findFragmentById(R.id.mainView)).commit()
                }
                return true
            }

        })

        actionSearchView?.setOnQueryTextListener(object : android.support.v7.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                val query = actionSearchView?.query.toString()
                val bundle: Bundle = Bundle()
                bundle.putString("searchQuery", query)
                val searchFragment = SearchFragment()
                searchFragment.arguments = bundle
                supportFragmentManager.beginTransaction()
                        .replace(R.id.mainView, searchFragment)
                        .addToBackStack(null)
                        .commit()
                return false
            }
        })

        return true

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
//            R.id.app_bar_search ->

            R.id.app_bar_profile -> {
                val intent = Intent(this, AccountActivity::class.java)
                startActivity(intent)
            }
            R.id.app_bar_discover -> {
                val intent = Intent(this, DiscoverActivity::class.java)
                startActivity(intent)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

}
