package com.alphae.rishi.towatch.Activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
import android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.alphae.rishi.towatch.Adapters.HomeAdapter
import com.alphae.rishi.towatch.Api.Clients.YouTubeClient
import com.alphae.rishi.towatch.Api.ServiceGenerator
import com.alphae.rishi.towatch.BuildConfig
import com.alphae.rishi.towatch.Fragments.BottomSheetFragment
import com.alphae.rishi.towatch.Fragments.SearchFragment
import com.alphae.rishi.towatch.POJOs.TmdbFind.Find
import com.alphae.rishi.towatch.POJOs.YouTube.YouTubeVideo
import com.alphae.rishi.towatch.R
import com.alphae.rishi.towatch.TmdbApi.TmdbApiClient
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
    private lateinit var client: TmdbApiClient

    private var gotSearchIntent: Boolean = false
    private var searchIntentQuery: String = ""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        client = ServiceGenerator.createService(TmdbApiClient::class.java)

        val window: Window = window
        window.clearFlags(FLAG_TRANSLUCENT_STATUS)
        window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.colorPrimary)
        window.navigationBarColor = resources.getColor(R.color.colorPrimary)
        val context = this

//        MobileAds.initialize(this, "ca-app-pub-3940256099942544/1033173712")
        MobileAds.initialize(this, BuildConfig.AdMobId)
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
        val firstTime = sharedPreferences.getBoolean("firstTime", true)
        if (firstTime) {
            val intent = Intent(this, WelcomeActivity::class.java)
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
                Log.d("Tried", "you")
                val value1 = extras.getString(Intent.EXTRA_TEXT)
                Log.d("value1",value1)
                if (value1 != null) {
                    if (value1.contains("https")) {
                        var videoId = value1.substringAfter("https://youtu.be/")
                        Log.d("videoId",videoId)
                        val ytClient = ServiceGenerator.createYtService(YouTubeClient::class.java)
                        val call = ytClient.getVideoTitle(videoId, BuildConfig.YoutubeApiKey)
                        call.enqueue(object : Callback<YouTubeVideo> {
                            override fun onFailure(call: Call<YouTubeVideo>?, t: Throwable?) {}
                            override fun onResponse(call: Call<YouTubeVideo>?, response: Response<YouTubeVideo>?) {
                                val videoTitle: String = response?.body()?.items!![0].snippet.title
                                gotSearchIntent = true
                                searchIntentQuery = modifyTitle(videoTitle)
                                searchMenuItem.expandActionView()
                                actionSearchView?.setQuery(searchIntentQuery, true)
                            }

                            private fun modifyTitle(title: String): String {
//                    val temp1 = title.split("official",true,0)[0]
                                var temp1 = title.replace("official", "*", true)
                                temp1 = temp1.replace("trailer", "*", true)
                                temp1 = temp1.replace("|", "*", true)
                                temp1 = temp1.replace("(", "*", true)
                                temp1 = temp1.replace("#", "*", true)
                                temp1 = temp1.replace("movie", "*", true)
                                return temp1.split("*")[0].trim()
                            }
                        })
                    } else {
                        val call = callMovieFind(value1)
                        call.enqueue(object : Callback<Find> {
                            override fun onFailure(call: Call<Find>?, t: Throwable?) {}
                            override fun onResponse(call: Call<Find>?, response: Response<Find>?) {
                                val body = response?.body()
                                if (body?.movieResults != null) {
                                    val intent = Intent(this@MainActivity, MovieDetailsActivity::class.java)
                                    intent.putExtra("movieId", body.movieResults[0].id)
                                    intent.putExtra("posterPath", body.movieResults[0].posterPath)
                                    startActivity(intent)
                                }
                            }
                        })
                    }
                }
            } catch (ex: Exception) {
                Log.d("Youtube", ex.toString())
            }
        }

        viewPager.adapter = HomeAdapter(this, supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
        val version = sharedPreferences.getString("version", "new")
        Log.d("version",version)
        if (version == "new") {
            Log.d("version",version)
            val alertDialog = android.support.v7.app.AlertDialog.Builder(this@MainActivity).create()
            alertDialog.setTitle("What's New")
            alertDialog.setMessage(resources.getString(R.string.whats_new))
            alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "OK", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog.show()
            val editor = sharedPreferences.edit()
            editor.putString("version", "old")
            editor.commit()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.actionbar_menu, menu)

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val searchItem = menu?.findItem(R.id.app_bar_search)
        searchMenuItem = searchItem!!
        actionSearchView = searchItem.actionView as android.support.v7.widget.SearchView
        val searchEditText = actionSearchView!!.findViewById<EditText>(android.support.v7.appcompat.R.id.search_src_text) as (EditText)
        searchEditText.setTextColor(getResources().getColor(R.color.accent))
        val searchClose = actionSearchView!!.findViewById(android.support.v7.appcompat.R.id.search_close_btn) as ImageView
        searchClose.setColorFilter(resources.getColor(R.color.accent))
        searchClose.setAlpha(255)
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

        if (gotSearchIntent) {
            searchMenuItem.expandActionView()
            actionSearchView?.setQuery(searchIntentQuery, true)
        }
        return true

    }

    private fun callMovieFind(id: String): Call<Find> {
        val call = client.getMovieFromId(
                id,
                "imdb_id",
                BuildConfig.TmdbApiKey
        )
        return call
    }

}
