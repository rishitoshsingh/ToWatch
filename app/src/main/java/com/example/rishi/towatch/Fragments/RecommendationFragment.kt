package com.example.rishi.towatch.Fragments

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.rishi.towatch.Adapters.RecommendationAdapter
import com.example.rishi.towatch.Api.ServiceGenerator
import com.example.rishi.towatch.BuildConfig
import com.example.rishi.towatch.Database.WatchDatabase
import com.example.rishi.towatch.Database.WatchList
import com.example.rishi.towatch.Database.WatchedList
import com.example.rishi.towatch.Listners.PaginationScrollListner
import com.example.rishi.towatch.POJOs.TmdbRecommendations.Recommendations
import com.example.rishi.towatch.R
import com.example.rishi.towatch.TmdbApi.TmdbApiClient
import kotlinx.android.synthetic.main.activity_movie_details.*
import kotlinx.android.synthetic.main.recycler_view.*
import kotlinx.android.synthetic.main.similar_recommendation.*
import retrofit2.Call
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [RecommendationFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [RecommendationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecommendationFragment : Fragment() {

    private var recommendedMovies: ArrayList<com.example.rishi.towatch.POJOs.TmdbRecommendations.Result> = ArrayList()
    private lateinit var client: TmdbApiClient
    private val PAGE_START = 1
    private var isLoading = false
    private var isLastPage = false
    private var TOTAL_PAGES = 2
    private var currentPage = PAGE_START
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var watchDatabase: WatchDatabase
    private var task: Int = 1
    private lateinit var data: WatchList
    private lateinit var watchedData: WatchedList
    private var presentInWatch: Boolean = false
    private var presentInWatched: Boolean = false

    private lateinit var mView:View


    private var mMovieId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMovieId = arguments!!.getLong("movieId")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.similar_recommendation, container, false)
    }

    companion object {
        fun newInstance(): RecommendationFragment {
            return RecommendationFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view
        view.findViewById<TextView>(R.id.similarRecommendationTitle).text = "Recommendations"

        client = ServiceGenerator.createService(TmdbApiClient::class.java)

        watchDatabase = WatchDatabase.getInstance(context!!)!!

        viewManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        viewAdapter = object : RecommendationAdapter(context!!, recommendedMovies) {
            override fun addMovie(movie: com.example.rishi.towatch.POJOs.TmdbRecommendations.Result) {
                task = 1
                data = WatchList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                FindMovie().execute(data.movieId)
            }

            override fun removeMovie(movie: com.example.rishi.towatch.POJOs.TmdbRecommendations.Result) {
                task = 2
                data = WatchList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                FindMovie().execute(data.movieId)
            }

            override fun watchedMovie(movie: com.example.rishi.towatch.POJOs.TmdbRecommendations.Result) {
                task = 3
                data = WatchList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                watchedData = WatchedList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                FindMovie().execute(data.movieId)
            }
        }
        similarRecommendationRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            itemAnimator = DefaultItemAnimator()
        }

        loadFirstPage()

    }

    private fun loadFirstPage() {
        val call = callRecommendedMovie()
        call.enqueue(object : retrofit2.Callback<Recommendations> {
            override fun onFailure(p0: Call<Recommendations>?, p1: Throwable?) {
                Log.v("loadFirstPage()", "failed", p1)
            }

            override fun onResponse(p0: Call<Recommendations>?, p1: Response<Recommendations>?) {

                val recommendations: Recommendations? = p1?.body()!!
                if(recommendations?.totalResults == 0.toLong()){
                    mView.visibility = View.GONE
//                    belowRecommendations.visibility = View.GONE
                }
                TOTAL_PAGES = recommendations?.totalPages?.toInt()!!
                recommendedMovies.clear()
                for (item in recommendations.results) recommendedMovies.add(item)
                viewAdapter.notifyDataSetChanged()
                isLoading = false
            }
        })
    }

    private fun callRecommendedMovie(): Call<Recommendations> {
        val call = client.getRecommendations(
                mMovieId.toInt(),
                BuildConfig.TmdbApiKey,
                currentPage
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
//            Snackbar.make(recyclerView, "Added to Watched Movies", Snackbar.LENGTH_SHORT).show()
        }
    }

    private inner class InsertMovie : AsyncTask<WatchList, Void, Void>() {
        override fun doInBackground(vararg params: WatchList?): Void? {
            val movie = params[0]
            watchDatabase.watchDaoAccess().insertMovie(movie!!)
            return null
        }

        override fun onPostExecute(result: Void?) {
//            Snackbar.make(recyclerView, "Added to Playlist", Snackbar.LENGTH_SHORT).show()
        }
    }

    private inner class RemoveMovie : AsyncTask<WatchList, Void, Void>() {
        override fun doInBackground(vararg params: WatchList?): Void? {
            val movie = params[0]
            watchDatabase.watchDaoAccess().deleteMovie(movie!!)
            return null
        }

        override fun onPostExecute(result: Void?) {
//            Snackbar.make(recyclerView, "Removed from Playlist", Snackbar.LENGTH_SHORT).show()
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
//                        Snackbar.make(recyclerView, "Movie Already in Watch List", Snackbar.LENGTH_SHORT).show()
                    }
                }
                2 -> {
                    if (!result) {
                        RemoveMovie().execute(data)
                    } else {
//                        Snackbar.make(recyclerView, "Movie not found in Watch List", Snackbar.LENGTH_SHORT).show()
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
//                            Snackbar.make(recyclerView, "Movie Already in Watched List", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}