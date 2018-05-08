package com.example.rishi.towatch.Activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
import android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
import android.widget.SearchView
import com.example.rishi.towatch.HomeAdapter
import com.example.rishi.towatch.R
import com.example.rishi.towatch.firebase.SignUpActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var actionSearchView: SearchView? = null


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(my_toolbar)
        val toolbar = supportActionBar

        val window: Window = window
        window.clearFlags(FLAG_TRANSLUCENT_STATUS)
        window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        viewPager.adapter = HomeAdapter(this,supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)

    }
//actionBar Menu

    fun createQuerystring(string: String?): String {
        var query: String = ""
        if (string == null) {
            return "move"
        } else {
            val strings = string.split(" ")
            for (str in strings) {
                query += str
                query += "%20"
            }
        }
        return query
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.actionbar_menu, menu)

        val searchItem = menu?.findItem(R.id.app_bar_search)
        actionSearchView = searchItem?.actionView as SearchView


//        actionSearchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextChange(newText: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                val query = createQuerystring(actionSearchView?.query.toString())
//                val client = ServiceGenerator.createService(TmdbApiClient::class.java)
//                val call = client.search(R.string.tmdb_key.toString(),
//                        "en-US",
//                        true,
//                        1,
//                        query)
//                call.enqueue(object : retrofit2.Callback<JsonA> {
//                    override fun onFailure(call: Call<JsonA>?, t: Throwable?) {
//                        Log.v("Search Failure", t.toString())
//                    }
//
//                    override fun onResponse(call: Call<JsonA>?, response: Response<JsonA>?) {
//                        val discoverMovie: JsonA = response?.body()!!
//                        movies.clear()
//                        for (item in discoverMovie.results) movies.add(item)
//                        viewAdapter.notifyDataSetChanged()
//                    }
//
//                })
//
//                return false
//            }
//        })

        return true

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
//            R.id.app_bar_search ->
//            R.id.app_bar_profile -> {
//                val intent = Intent(this, SignUpActivity::class.java)
//                startActivity(intent)
//            }
            R.id.app_bar_profile -> {
                val intent = Intent(this, WatchListActivity::class.java)
                startActivity(intent)
            }
            else -> return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

}
