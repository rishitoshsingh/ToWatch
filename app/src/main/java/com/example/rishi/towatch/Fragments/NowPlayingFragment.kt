package com.example.rishi.towatch.Fragments


import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
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
import com.example.rishi.towatch.POJOs.Tmdb.JsonB
import com.example.rishi.towatch.POJOs.Tmdb.Result
import com.example.rishi.towatch.R
import com.example.rishi.towatch.TmdbApi.TmdbApiClient
import com.facebook.ads.*
import kotlinx.android.synthetic.main.recycler_view.*
import retrofit2.Call
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 * Use the [NowPlayingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NowPlayingFragment : Fragment() {
    private var nowPlayingMovies: ArrayList<kotlin.Any> = ArrayList<kotlin.Any>()
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

    private lateinit var region: String
    private lateinit var language: String

    private var lastAdPosition: Int = -1
    private val ADS_PER_ITEMS: Int = 9

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.recycler_view, container, false)
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

        val sharedPreferences: SharedPreferences = activity?.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)!!
        region = sharedPreferences.getString("region", "US")
        language = sharedPreferences.getString("language", "en-US")


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

        viewAdapter = object : MovieAdapter(context!!, nowPlayingMovies) {
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
            override fun getCurrentPage() = currentPage
            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1
                if (refresh_layout != null) refresh_layout.isRefreshing = true
                loadNextPage()
            }

            override fun getTotalPageCount() = TOTAL_PAGES
            override fun isLastPage() = isLastPage
            override fun isLoading() = isLoading
        })

        if (refresh_layout != null) {
            refresh_layout.isRefreshing = true
        }

//        nativeAd = NativeAd(this.context, "YOUR_PLACEMENT_ID")
//        nativeAd.setAdListener(object : NativeAdListener {
//            override fun onMediaDownloaded(p0: Ad?) {}
//            override fun onError(ad: Ad, adError: AdError) {}
//            override fun onAdLoaded(ad: Ad) {
//                addAds(ad)
//            }
//
//            override fun onAdClicked(ad: Ad) {}
//            override fun onLoggingImpression(ad: Ad) {}
//        })

        loadFirstPage()

        refresh_layout.setOnRefreshListener {
            val temp: SharedPreferences = activity?.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)!!
            region = temp.getString("region", "US")
            language = temp.getString("language", "en-US")
            if (shimmer_container != null) {
                shimmer_container.stopShimmerAnimation()
                shimmer_container.visibility = View.GONE
            }
            nowPlayingMovies.removeAll(nowPlayingMovies)
            isLoading = false
            isLastPage = false
            TOTAL_PAGES = 2
            currentPage = PAGE_START
            task = 1
            loadFirstPage()
        }
    }

    private fun loadAdsToList() {
//        AdSettings.addTestDevice("aab16a2b-c590-4f73-b619-fc9d7f8e37b1")
//        try {
        Log.d("load Ads","inside")
            val nativeAdsManager = NativeAdsManager(activity!!, "YOUR_PLACEMENT_ID", 2)
            nativeAdsManager.setListener(object : NativeAdsManager.Listener {
                override fun onAdError(adError: AdError) {
                    Log.d("AdError",adError.toString())
                }

                override fun onAdsLoaded() {
                    try {
                        while (lastAdPosition + ADS_PER_ITEMS < nowPlayingMovies.size) {
                            val nextNativeAd = nativeAdsManager.nextNativeAd()
                            lastAdPosition += ADS_PER_ITEMS
                            Log.d("AdLoaded fb","1")
                            nowPlayingMovies.add(lastAdPosition, nextNativeAd)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.d("Error Ad fb",e.toString())
                    }
                    viewAdapter.notifyDataSetChanged()
                }
            })
            nativeAdsManager.loadAds()
//        } catch (e: Exception) {
//            val str = "TAG"
//            val stringBuilder = StringBuilder()
//            stringBuilder.append("loadAdsToList: ")
//            stringBuilder.append(e.toString())
//            Log.e(str, stringBuilder.toString())
//        }
    }


    private fun loadFirstPage() {
        val call = callDiscoverMovie()
        call.enqueue(object : retrofit2.Callback<JsonB> {
            override fun onFailure(p0: Call<JsonB>?, p1: Throwable?) {
                Log.v("loadFirstPage()", "failed", p1)
            }

            override fun onResponse(p0: Call<JsonB>?, p1: Response<JsonB>?) {
                val jsonB: JsonB? = p1?.body()!!

                if (shimmer_container != null) {
                    shimmer_container.stopShimmerAnimation()
                    shimmer_container.visibility = View.GONE
                }

                TOTAL_PAGES = jsonB?.totalPages?.toInt()!!
                nowPlayingMovies.clear()
                for (item in jsonB.results) nowPlayingMovies.add(item)
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
        call.enqueue(object : retrofit2.Callback<JsonB> {
            override fun onFailure(p0: Call<JsonB>?, p1: Throwable?) {
                Log.v("temp", "failed Next Page", p1)
            }

            override fun onResponse(p0: Call<JsonB>?, p1: Response<JsonB>?) {
                val jsonB: JsonB = p1?.body()!!

                val totalResults = jsonB.results?.size
//                for (i in 0..totalResults!! step 5) totalAdsToBeLoaded++

                for (item in jsonB.results) nowPlayingMovies.add(item)
                viewAdapter.notifyDataSetChanged()

                loadAdsToList()

                isLoading = false
                if (refresh_layout != null) {
                    refresh_layout.isRefreshing = false
                }
            }
        })

    }

    private fun callDiscoverMovie(): Call<JsonB> {
        val call = client.getNowPlaying(
                BuildConfig.TmdbApiKey,
                language,
                currentPage,
                region
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
