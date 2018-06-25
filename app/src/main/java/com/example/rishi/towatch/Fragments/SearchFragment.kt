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
import com.example.rishi.towatch.Adapters.MovieAdapter
import com.example.rishi.towatch.Api.ServiceGenerator
import com.example.rishi.towatch.BuildConfig
import com.example.rishi.towatch.Database.WatchDatabase
import com.example.rishi.towatch.Database.WatchList
import com.example.rishi.towatch.Database.WatchedList
import com.example.rishi.towatch.Listners.PaginationScrollListner
import com.example.rishi.towatch.POJOs.Tmdb.JsonA
import com.example.rishi.towatch.POJOs.Tmdb.Result

import com.example.rishi.towatch.R
import com.example.rishi.towatch.TmdbApi.TmdbApiClient
import com.facebook.ads.AdError
import com.facebook.ads.NativeAdsManager
import kotlinx.android.synthetic.main.recycler_view.*
import retrofit2.Call
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SearchFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {

    private var searchMovies: ArrayList<kotlin.Any> = ArrayList<kotlin.Any>()
    private lateinit var client: TmdbApiClient
    private val PAGE_START = 1
    private var isLoading = false
    private var isLastPage = false
    private var TOTAL_PAGES = 2
    private var currentPage = PAGE_START
    private lateinit var viewAdapter: MovieAdapter
    private lateinit var viewManager: GridLayoutManager
    private lateinit var watchDatabase: WatchDatabase
    private var task: Int = 1
    private lateinit var data: WatchList
    private lateinit var watchedData: WatchedList
    private var presentInWatch: Boolean = false
    private var presentInWatched: Boolean = false

    private var searchQuery:String = ""

    private var lastAdPosition: Int = -1
    private val ADS_PER_ITEMS: Int = 5

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.recycler_view, container, false)
    }

    companion object {
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchQuery = arguments?.getString("searchQuery")!!
    }

    override fun onResume() {
        shimmer_container.startShimmerAnimation()
        super.onResume()
    }

    override fun onPause() {
        shimmer_container.stopShimmerAnimation()
        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        client = ServiceGenerator.createService(TmdbApiClient::class.java)

        watchDatabase = WatchDatabase.getInstance(context!!)!!

        viewManager = GridLayoutManager(context, 2)
        viewManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (viewAdapter.getItemViewType(position)) {
                    viewAdapter.MOVIE -> 1
                    viewAdapter.NATIVE_AD -> (viewManager).spanCount
                    else -> 1
                }
            }
        }
        viewAdapter = object : MovieAdapter(context!!, searchMovies) {
            override fun addMovie(movie: Result) {
                task = 1
                data = WatchList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                if (refresh_layout != null) {
                    refresh_layout.isRefreshing = true
                }
                FindMovie().execute(data.movieId)
            }

            override fun removeMovie(movie: Result) {
                task = 2
                data = WatchList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                if (refresh_layout != null) {
                    refresh_layout.isRefreshing = true
                }
                FindMovie().execute(data.movieId)
            }

            override fun watchedMovie(movie: Result) {
                task = 3
                data = WatchList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                watchedData = WatchedList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                if (refresh_layout != null) {
                    refresh_layout.isRefreshing = true
                }
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
                if (refresh_layout != null) {
                    refresh_layout.isRefreshing = true
                }
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
        if (refresh_layout != null) {
            refresh_layout.isRefreshing = true
        }

        loadFirstPage()

        refresh_layout.setOnRefreshListener {
            if (shimmer_container != null) {
                shimmer_container.stopShimmerAnimation()
                shimmer_container.visibility = View.VISIBLE
            }
            searchMovies.removeAll(searchMovies)
            isLoading = false
            isLastPage = false
            TOTAL_PAGES = 2
            currentPage = PAGE_START
            task = 1
            loadFirstPage()
            refresh_layout.isRefreshing = false
        }

    }

    private fun loadAdsToList() {
        try {
            val nativeAdsManager = NativeAdsManager(activity!!, "YOUR_PLACEMENT_ID", 2)
            nativeAdsManager.setListener(object : NativeAdsManager.Listener {
                override fun onAdError(adError: AdError) {
                }

                override fun onAdsLoaded() {
                    try {
                        while (lastAdPosition + ADS_PER_ITEMS < searchMovies.size) {
                            val nextNativeAd = nativeAdsManager.nextNativeAd()
                            lastAdPosition += ADS_PER_ITEMS
                            Log.d("AdLoaded fb", "1")
                            searchMovies.add(lastAdPosition, nextNativeAd)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.d("Error Ad fb", e.toString())
                    }
                    viewAdapter.notifyDataSetChanged()
                }
            })
            nativeAdsManager.loadAds()
        } catch (e: Exception) {
            val str = "TAG"
            val stringBuilder = StringBuilder()
            stringBuilder.append("loadAdsToList: ")
            stringBuilder.append(e.toString())
            Log.e(str, stringBuilder.toString())
        }
    }

    private fun loadFirstPage() {
        val call = callDiscoverMovie()
        call.enqueue(object : retrofit2.Callback<JsonA> {
            override fun onFailure(p0: Call<JsonA>?, p1: Throwable?) {
                Log.v("loadFirstPage()", "failed", p1)
            }

            override fun onResponse(p0: Call<JsonA>?, p1: Response<JsonA>?) {

                shimmer_container.stopShimmerAnimation()
                shimmer_container.visibility = View.GONE

                val jsonA: JsonA? = p1?.body()!!

                TOTAL_PAGES = jsonA?.totalPages?.toInt()!!
                searchMovies.clear()
                for (item in jsonA.results) searchMovies.add(item)
                viewAdapter.notifyDataSetChanged()
                loadAdsToList()
                isLoading = false
                if (refresh_layout != null) {
                    refresh_layout.isRefreshing = false
                }
            }
        })
    }

    private fun loadNextPage() {

        val call = callDiscoverMovie()
        call.enqueue(object : retrofit2.Callback<JsonA> {
            override fun onFailure(p0: Call<JsonA>?, p1: Throwable?) {
                Log.v("temp", "failed Next Page", p1)
            }

            override fun onResponse(p0: Call<JsonA>?, p1: Response<JsonA>?) {
                val jsonA: JsonA = p1?.body()!!
                for (item in jsonA.results) searchMovies.add(item)
                viewAdapter.notifyDataSetChanged()
                loadAdsToList()
                isLoading = false
                if (refresh_layout != null) {
                    refresh_layout.isRefreshing = false
                }
            }
        })

    }

    private fun callDiscoverMovie(): Call<JsonA> {
        val call = client.search(
                BuildConfig.TmdbApiKey,
                null,
                false,
                currentPage,
                searchQuery
        )
        return call
    }


    override fun onDestroy() {
        WatchDatabase.destroyInstance()
        super.onDestroy()
    }

    private inner class InsertWatchedMovie : AsyncTask<WatchedList, Void, Void>() {
        override fun doInBackground(vararg params: WatchedList?): Void? {
            val movie = params[0]
            watchDatabase.watchedDaoAccess().insertMovie(movie!!)
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (refresh_layout != null) {
                refresh_layout.isRefreshing = false
            }
            Snackbar.make(recyclerView, "Added to Watched Movies", Snackbar.LENGTH_SHORT).show()
        }
    }

    private inner class InsertMovie : AsyncTask<WatchList, Void, Void>() {
        override fun doInBackground(vararg params: WatchList?): Void? {
            val movie = params[0]
            watchDatabase.watchDaoAccess().insertMovie(movie!!)
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (refresh_layout != null) {
                refresh_layout.isRefreshing = false
            }
            Snackbar.make(recyclerView, "Added to Playlist", Snackbar.LENGTH_SHORT).show()
        }
    }

    private inner class RemoveMovie : AsyncTask<WatchList, Void, Void>() {
        override fun doInBackground(vararg params: WatchList?): Void? {
            val movie = params[0]
            watchDatabase.watchDaoAccess().deleteMovie(movie!!)
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (refresh_layout != null) {
                refresh_layout.isRefreshing = false
            }
            Snackbar.make(recyclerView, "Removed from Playlist", Snackbar.LENGTH_SHORT).show()
        }
    }

    private inner class FindMovie : AsyncTask<Long, Void, Boolean>() {
        override fun doInBackground(vararg params: Long?): Boolean {
            val movieId = params[0]
            when (task) {
                1, 3 -> {
                    val movieList = watchDatabase.watchDaoAccess().fetchMovie(movieId!!)
                    presentInWatch = !movieList.isEmpty()
                    val watchedList = watchDatabase.watchedDaoAccess().fetchMovie(movieId)
                    presentInWatched = !watchedList.isEmpty()
                    return movieList.isEmpty() and watchedList.isEmpty()
                }
                2 -> {
                    val movieList = watchDatabase.watchDaoAccess().fetchMovie(movieId!!)
                    return movieList.isEmpty()
                }
            }
            return false
        }

        override fun onPostExecute(result: Boolean) {
            when (task) {
                1 -> {
                    if (result) {
                        InsertMovie().execute(data)
                    } else {
                        if (refresh_layout != null) {
                            refresh_layout.isRefreshing = false
                        }
                        Snackbar.make(recyclerView, "Movie Already in Watch List", Snackbar.LENGTH_SHORT).show()
                    }
                }
                2 -> {
                    if (!result) {
                        RemoveMovie().execute(data)
                    } else {
                        if (refresh_layout != null) {
                            refresh_layout.isRefreshing = false
                        }
                        Snackbar.make(recyclerView, "Movie not found in Watch List", Snackbar.LENGTH_SHORT).show()
                    }
                }
                3 -> {
                    if (result) {
                        InsertWatchedMovie().execute(watchedData)
                    } else {
                        if (presentInWatch) {
                            RemoveMovie().execute(data)
                            InsertWatchedMovie().execute(watchedData)
                        } else {
                            if (refresh_layout != null) {
                                refresh_layout.isRefreshing = false
                            }
                            Snackbar.make(recyclerView, "Movie Already in Watched List", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}