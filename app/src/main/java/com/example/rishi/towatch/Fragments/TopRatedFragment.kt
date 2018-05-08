package com.example.rishi.towatch.Fragments


import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rishi.towatch.Api.ServiceGenerator
import com.example.rishi.towatch.Database.WatchDatabase
import com.example.rishi.towatch.Database.WatchList
import com.example.rishi.towatch.MovieAdapter
import com.example.rishi.towatch.POJOs.Tmdb.JsonA
import com.example.rishi.towatch.POJOs.Tmdb.Result
import com.example.rishi.towatch.PaginationScrollListner

import com.example.rishi.towatch.R
import com.example.rishi.towatch.TmdbApi.TmdbApiClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.recycler_view.*
import retrofit2.Call
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 */
class TopRatedFragment : Fragment() {

    private var topRatedMovies: ArrayList<Result> = ArrayList<Result>()
    private lateinit var client: TmdbApiClient
    private val PAGE_START = 1
    private var isLoading = false
    private var isLastPage = false
    private var TOTAL_PAGES = 2
    private var currentPage = PAGE_START
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var watchDatabase: WatchDatabase
    private var task:Int = 1
    private lateinit var data:WatchList


    companion object {

        fun newInstance(): DiscoverFragment {
            return DiscoverFragment()
        }
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.recycler_view, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        client = ServiceGenerator.createService(TmdbApiClient::class.java)

        watchDatabase = WatchDatabase.getInstance(context)!!

        viewManager = GridLayoutManager(context, 2)
        viewAdapter = object : MovieAdapter(context, topRatedMovies){
            override fun addMovie(movie: Result) {
                task = 1
                data = WatchList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                FindMovie().execute(data.movieId)
            }

            override fun removeMovie(movie: Result) {
                task = 2
                data = WatchList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                FindMovie().execute(data.movieId)
            }

        }
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
        val call = callTopRatedMovie()
        call.enqueue(object : retrofit2.Callback<JsonA> {
            override fun onFailure(p0: Call<JsonA>?, p1: Throwable?) {
                Log.v("loadFirstPage()", "failed", p1)
            }

            override fun onResponse(p0: Call<JsonA>?, p1: Response<JsonA>?) {

                val jsonA: JsonA? = p1?.body()!!

                TOTAL_PAGES = jsonA?.totalPages?.toInt()!!
                topRatedMovies.clear()
                for (item in jsonA.results) topRatedMovies.add(item)
                viewAdapter.notifyDataSetChanged()
                isLoading = false

            }
        })
    }

    private fun loadNextPage() {

        val call = callTopRatedMovie()
        call.enqueue(object : retrofit2.Callback<JsonA> {
            override fun onFailure(p0: Call<JsonA>?, p1: Throwable?) {
                Log.v("temp", "failed Next Page", p1)
            }

            override fun onResponse(p0: Call<JsonA>?, p1: Response<JsonA>?) {
                val jsonA: JsonA = p1?.body()!!
                for (item in jsonA.results) topRatedMovies.add(item)
                viewAdapter.notifyDataSetChanged()
                isLoading = false
            }
        })

    }

    private fun callTopRatedMovie(): Call<JsonA> {
        val call = client.getToprated(
                resources.getString(R.string.tmdb_key),
                "en-US",
                currentPage,
                null)
        return call
    }

    private inner class InsertMovie : AsyncTask<WatchList, Void, Void>(){
        override fun doInBackground(vararg params: WatchList?): Void? {
            val movie = params[0]
            watchDatabase.daoAccess().insertMovie(movie!!)
            return null
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: Void?) {
            Snackbar.make(recyclerView,"Added to Playlist", Snackbar.LENGTH_SHORT).show()
        }
    }

    private inner class RemoveMovie : AsyncTask<WatchList, Void, Void>() {
        override fun doInBackground(vararg params: WatchList?): Void? {
            val movie = params[0]
            watchDatabase.daoAccess().deleteMovie(movie!!)
            return null
        }

        override fun onPostExecute(result: Void?) {
            Snackbar.make(recyclerView, "Removed from Playlist", Snackbar.LENGTH_SHORT).show()
        }
    }

    private inner class FindMovie : AsyncTask<Long, Void, Boolean>() {
        override fun doInBackground(vararg params: Long?): Boolean {
            val movieId = params[0]
            val movieList = watchDatabase.daoAccess().fetchMovie(movieId!!)
            return !movieList.isEmpty()
        }

        override fun onPostExecute(result: Boolean) {
            if(task == 1){
                if(!result){
                    InsertMovie().execute(data)
                } else {
                    Snackbar.make(recyclerView,"Movie Already in Watch List",Snackbar.LENGTH_SHORT).show()
                }
            } else {
                if(result){
                    RemoveMovie().execute(data)
                } else {
                    Snackbar.make(recyclerView,"Movie not found in Watch List",Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

}// Required empty public constructor
