package com.example.rishi.towatch.Fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rishi.towatch.Api.ServiceGenerator
import com.example.rishi.towatch.MovieAdapter
import com.example.rishi.towatch.POJOs.Tmdb.JsonB
import com.example.rishi.towatch.POJOs.Tmdb.Result
import com.example.rishi.towatch.PaginationScrollListner

import com.example.rishi.towatch.R
import com.example.rishi.towatch.TmdbApi.TmdbApiClient
import kotlinx.android.synthetic.main.recycler_view.*
import retrofit2.Call
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 */
class UpcomingFragment : Fragment() {
    private var upcomingMovies: ArrayList<Result> = ArrayList<Result>()
    private lateinit var client: TmdbApiClient
    private val PAGE_START = 1
    private var isLoading = false
    private var isLastPage = false
    private var TOTAL_PAGES = 2
    private var currentPage = PAGE_START
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.recycler_view, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        client = ServiceGenerator.createService(TmdbApiClient::class.java)

        viewManager = GridLayoutManager(context, 2)
        viewAdapter = MovieAdapter(context, upcomingMovies)
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
        val call = callUpcomingMovies()
        call.enqueue(object : retrofit2.Callback<JsonB> {
            override fun onFailure(p0: Call<JsonB>?, p1: Throwable?) {
                Log.v("loadFirstPage()", "failed", p1)
            }

            override fun onResponse(p0: Call<JsonB>?, p1: Response<JsonB>?) {

                val jsonB: JsonB? = p1?.body()!!

                TOTAL_PAGES = jsonB?.totalPages?.toInt()!!
                upcomingMovies.clear()
                for (item in jsonB.results) upcomingMovies.add(item)
                viewAdapter.notifyDataSetChanged()
                isLoading = false

            }
        })
    }

    private fun loadNextPage() {

        val call = callUpcomingMovies()
        call.enqueue(object : retrofit2.Callback<JsonB> {
            override fun onFailure(p0: Call<JsonB>?, p1: Throwable?) {
                Log.v("temp", "failed Next Page", p1)
            }

            override fun onResponse(p0: Call<JsonB>?, p1: Response<JsonB>?) {
                val jsonB: JsonB = p1?.body()!!
                for (item in jsonB.results) upcomingMovies.add(item)
                viewAdapter.notifyDataSetChanged()
                isLoading = false
            }
        })

    }

    private fun callUpcomingMovies(): Call<JsonB> {
        val call = client.getUpcoming(
                resources.getString(R.string.tmdb_key),
                "en-US",
                currentPage,
                null
        )
        return call
    }


}