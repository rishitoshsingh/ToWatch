package com.example.rishi.towatch

import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
import android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
import android.widget.AdapterView
import android.widget.SearchView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*


val TMDB_SEARCH_BASE_URL = "https://api.themoviedb.org/3/search/movie?api_key="
val TMDB_KEY = "cc4b67c52acb514bdf4931f7cedfd12b"

val BASE_URL = "https://api.themoviedb.org/3/discover/movie?api_key="   
val API_KEY = "cc4b67c52acb514bdf4931f7cedfd12b"
var PAGE = 1
val RELEASE_YEAR = 2018
val LANGUAGE = "en-US"
val API_URL = BASE_URL + API_KEY + "&language=" + LANGUAGE + "&sort_by=popularity.desc&include_adult=true&include_video=true&page=" + PAGE.toString() + "&primary_release_year=" + RELEASE_YEAR.toString()
var mAdapter: CustomAdapter? = null
var movies: ArrayList<Movie> = ArrayList<Movie>()
class MainActivity : AppCompatActivity() {

    var actionSearchView: SearchView? = null


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(my_toolbar)

        val window: Window = window

        window.clearFlags(FLAG_TRANSLUCENT_STATUS)
        window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        mAdapter = CustomAdapter(this, ArrayList())
        gridview.adapter = mAdapter
        movieAsyncTask().execute(API_URL)


        gridview?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, MovieDetailsActivity::class.java)
            intent.putExtra("movie",movies.get(position))
            startActivity(intent)

        }

    }


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


        actionSearchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                val query = createQuerystring(actionSearchView?.query.toString())
                val URL = TMDB_SEARCH_BASE_URL + TMDB_KEY + "&language=" + LANGUAGE + "&query=" + query + "&page=" + PAGE.toString() + "&include_adult=true"
                movieSearchAsyncTask().execute(URL)
                return false
            }
        })

//        searchItem.setOnActionExpandListener(object : MenuItemCompat.OnActionExpandListener){
//
//        }

        return true

    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.app_bar_search -> true
        else -> false
    }

    class YoutubeAsyncTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            return QueryYoutube().getYoutubeResponse(params[0])
        }

        override fun onPostExecute(result: String?) {

            searchView?.setText(result, TextView.BufferType.EDITABLE)
            Log.i("Async task ", "Data Received")
        }
    }

    class movieSearchAsyncTask : AsyncTask<String, Void, ArrayList<Movie>>() {
        override fun doInBackground(vararg params: String?): ArrayList<Movie> {
            return QueryMovie().getMovieResponse(params[0])
        }

        override fun onPostExecute(result: ArrayList<Movie>?) {
            mAdapter?.clear()
            if (result != null && !result.isEmpty()){
                mAdapter?.addAll(result)
                movies.clear()
                movies = result
            }
            Log.v("Search Movie", "Data Received")
        }
    }


    class movieAsyncTask : AsyncTask<String, Void, ArrayList<Movie>>() {
        override fun doInBackground(vararg params: String?): ArrayList<Movie> {
            return QueryMovie().getMovieResponse(params[0])
        }

        override fun onPostExecute(result: ArrayList<Movie>?) {
            mAdapter?.clear()
            if (result != null && !result.isEmpty()) {
                mAdapter?.addAll(result)
                movies.clear()
                movies = result
            }
            Log.v("AsyncTask", "Data Received")
        }
    }

}

