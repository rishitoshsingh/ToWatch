package com.alphae.rishi.towatch.Activities

import android.content.Context
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
import android.widget.Toast
import com.alphae.rishi.towatch.Adapters.HomeAdapter
import com.alphae.rishi.towatch.Api.Clients.YouTubeClient
import com.alphae.rishi.towatch.Api.ServiceGenerator
import com.alphae.rishi.towatch.BuildConfig
import com.alphae.rishi.towatch.Fragments.BottomSheetFragment
import com.alphae.rishi.towatch.Fragments.SearchFragment
import com.alphae.rishi.towatch.POJOs.YouTube.YouTubeVideo
import com.alphae.rishi.towatch.R
import com.alphae.rishi.towatch.Utils.CrossfadeDrawer
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private var actionSearchView: android.support.v7.widget.SearchView? = null
    private lateinit var searchMenuItem: MenuItem
    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var mInterstitialAd: InterstitialAd

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val window: Window = window
        window.clearFlags(FLAG_TRANSLUCENT_STATUS)
        window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.colorPrimary)
        val context = this

//        MobileAds.initialize(this, "ca-app-pub-3940256099942544/1033173712")
        MobileAds.initialize(this, BuildConfig.AdmobInterstitial)
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = BuildConfig.AdmobInterstitial
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        val sharedPreferences: SharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        if (!sharedPreferences.contains("firstTime")) {
            Toast.makeText(this, "Welcome toWatch", Toast.LENGTH_LONG).show()
            val sharedPreferenceEditor: SharedPreferences.Editor = sharedPreferences.edit()
//            sharedPreferenceEditor.putBoolean("firstTime", true)
            sharedPreferenceEditor.putString("region", "US")
            sharedPreferenceEditor.putString("language", "en-US")
            sharedPreferenceEditor.commit()
        }
        val firstTime = sharedPreferences.getBoolean("firstTime",true)
        if (firstTime){
            val intent = Intent(this,WelcomeActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        }
        val drawer = object : CrossfadeDrawer(context, my_toolbar, context, savedInstanceState, 0) {
            override fun showBottomSheetFragment() {
                val bottomSheetFragment = BottomSheetFragment()
                bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
            }
        }.getCrossfadeDrawer()


        setSupportActionBar(my_toolbar)
        val toolbar = supportActionBar

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
                        actionSearchView?.setQuery(modifyTitle(videoTitle), true)
                    }

                    private fun modifyTitle(title: String): String {
//                    val temp1 = title.split("official",true,0)[0]
                        var temp1 = title.replace("official", "*", true)
                        temp1 = temp1.replace("trailer", "*", true)
                        temp1 = temp1.replace("|", "*", true)
                        temp1 = temp1.replace("#", "*", true)
                        temp1 = temp1.replace("movie", "*", true)
                        return temp1.split("*")[0].trim()
                    }
                })
            } catch (ex: Exception) {
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
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                val sharedPreferences = getSharedPreferences("Interstitial", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putInt("KeyEvents", 2)
                editor.commit()
                mInterstitialAd.adListener = object : AdListener() {
                    override fun onAdClosed() {
                        mInterstitialAd.loadAd(AdRequest.Builder().build())
                    }
                }
                if (mInterstitialAd.isLoaded) mInterstitialAd.show()
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

}
