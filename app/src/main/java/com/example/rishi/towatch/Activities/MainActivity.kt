package com.example.rishi.towatch.Activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
import android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
import android.widget.SearchView
import android.widget.Toast
import com.example.rishi.towatch.Api.ServiceGenerator
import com.example.rishi.towatch.CustomAdapter
import com.example.rishi.towatch.MovieAdapter
import com.example.rishi.towatch.POJOs.TmdbDiscover.DiscoverMovie
import com.example.rishi.towatch.POJOs.TmdbDiscover.Result
import com.example.rishi.towatch.PaginationScrollListner
import com.example.rishi.towatch.R
import com.example.rishi.towatch.TmdbApi.TmdbApiClient
import com.example.rishi.towatch.firebase.SignUpActivity
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    val TMDB_KEY = "cc4b67c52acb514bdf4931f7cedfd12b"
    var movies: ArrayList<Result> = ArrayList<Result>()
    private lateinit var client: TmdbApiClient
    private val PAGE_START = 1
    private var isLoading = false
    private var isLastPage = false
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private var TOTAL_PAGES = 5
    private var currentPage = PAGE_START


    //    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

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

        client = ServiceGenerator.createService(TmdbApiClient::class.java)

        viewManager = GridLayoutManager(this, 2)
        viewAdapter = MovieAdapter(this, movies)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            itemAnimator = DefaultItemAnimator()
        }
        recyclerView.addOnScrollListener(object : PaginationScrollListner(viewManager as GridLayoutManager) {
            override fun getCurrentPage(): Int {
                return currentPage
            }

            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1
                loadNextPage()

            }

            override fun getTotalPageCount(): Int {
                return TOTAL_PAGES
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

        })


        loadFirstPage()

    }


    private fun loadFirstPage() {
        val call = callDiscoverMovie()
        call.enqueue(object : retrofit2.Callback<DiscoverMovie> {
            override fun onFailure(p0: Call<DiscoverMovie>?, p1: Throwable?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Log.v("loadFirstPage()", "failed", p1)
            }

            override fun onResponse(p0: Call<DiscoverMovie>?, p1: Response<DiscoverMovie>?) {
                Toast.makeText(this@MainActivity,"GotFirstPageResponse",Toast.LENGTH_LONG).show()
                val discoverMovie: DiscoverMovie = p1?.body()!!
                TOTAL_PAGES = discoverMovie.totalPages.toInt()
                movies.clear()
                for (item in discoverMovie.results) movies.add(item)
                viewAdapter.notifyDataSetChanged()
                isLoading = false
            }
        })
    }

    private fun loadNextPage() {

        val call = callDiscoverMovie()
        call.enqueue(object : retrofit2.Callback<DiscoverMovie> {
            override fun onFailure(p0: Call<DiscoverMovie>?, p1: Throwable?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Log.v("temp", "failed Next Page", p1)
            }

            override fun onResponse(p0: Call<DiscoverMovie>?, p1: Response<DiscoverMovie>?) {
                val discoverMovie: DiscoverMovie = p1?.body()!!
                for (item in discoverMovie.results) movies.add(item)
                viewAdapter.notifyDataSetChanged()
                isLoading = false
            }
        })

    }

    private fun callDiscoverMovie(): Call<DiscoverMovie> {
        val call = client.getDiscoverMovie(
                TMDB_KEY,
                "en-US",
                null,
                true,
                true,
                currentPage
        )
        return call
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


        actionSearchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                val query = createQuerystring(actionSearchView?.query.toString())
                val client = ServiceGenerator.createService(TmdbApiClient::class.java)
                val call = client.search(query)
                call.enqueue(object : retrofit2.Callback<DiscoverMovie> {
                    override fun onFailure(call: Call<DiscoverMovie>?, t: Throwable?) {
                        Log.v("Search Failure", t.toString())
                    }

                    override fun onResponse(call: Call<DiscoverMovie>?, response: Response<DiscoverMovie>?) {
                        val discoverMovie: DiscoverMovie = response?.body()!!
                        movies.clear()
                        for (item in discoverMovie.results) movies.add(item)
                        viewAdapter.notifyDataSetChanged()
                    }

                })

                return false
            }
        })

        return true

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
//            R.id.app_bar_search ->
            R.id.app_bar_profile -> {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }
            else -> return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

}

