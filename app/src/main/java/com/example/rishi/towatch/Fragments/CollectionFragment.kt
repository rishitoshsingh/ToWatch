package com.example.rishi.towatch.Fragments

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.graphics.Palette
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.example.rishi.towatch.Adapters.CollectionAdapter
import com.example.rishi.towatch.Api.ServiceGenerator
import com.example.rishi.towatch.BuildConfig
import com.example.rishi.towatch.Database.WatchDatabase
import com.example.rishi.towatch.Database.WatchList
import com.example.rishi.towatch.Database.WatchedList
import com.example.rishi.towatch.POJOs.TmdbCollection.Collection
import com.example.rishi.towatch.POJOs.TmdbCollection.Part
import com.example.rishi.towatch.R
import com.example.rishi.towatch.TmdbApi.TmdbApiClient
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.collection_layout.*
import retrofit2.Call
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CollectionFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CollectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CollectionFragment : Fragment() {

    private val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500/"

    private var collectionMovies: ArrayList<Part> = ArrayList<Part>()
    private lateinit var client: TmdbApiClient
    private val PAGE_START = 1
    private var isLoading = false
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var watchDatabase: WatchDatabase
    private var task: Int = 1
    private lateinit var data: WatchList
    private lateinit var watchedData: WatchedList
    private var presentInWatch: Boolean = false
    private var presentInWatched: Boolean = false

    private lateinit var mView: View


    private var collectionId: Long = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.collection_layout, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        collectionId = arguments?.getLong("collectionId")!!
        super.onCreate(savedInstanceState)
    }

    companion object {
        fun newInstance(): CollectionFragment {
            return CollectionFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view

        client = ServiceGenerator.createService(TmdbApiClient::class.java)

        watchDatabase = WatchDatabase.getInstance(context!!)!!

        viewManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        viewAdapter = object : CollectionAdapter(context!!, collectionMovies) {
            override fun addMovie(movie: Part) {
                task = 1
                data = WatchList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                FindMovie().execute(data.movieId)
            }

            override fun removeMovie(movie: Part) {
                task = 2
                data = WatchList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                FindMovie().execute(data.movieId)
            }

            override fun watchedMovie(movie: Part) {
                task = 3
                data = WatchList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                watchedData = WatchedList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                FindMovie().execute(data.movieId)
            }
        }
        collectionRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            itemAnimator = DefaultItemAnimator()
        }

        loadFirstPage()

    }

    private fun loadFirstPage() {
        val call = callCollectionMovie()
        call.enqueue(object : retrofit2.Callback<Collection> {
            override fun onFailure(p0: Call<Collection>?, p1: Throwable?) {
                Log.v("loadFirstPage()", "failed", p1)
            }

            override fun onResponse(p0: Call<Collection>?, p1: Response<Collection>?) {
                val collection: Collection? = p1?.body()!!
                val posterUri = Uri.parse(IMAGE_BASE_URL + collection?.posterPath)
                val bannerUri = Uri.parse(IMAGE_BASE_URL + collection?.backdropPath)
                Glide.with(this@CollectionFragment)
                        .load(posterUri)
                        .listener(object : RequestListener<Drawable> {
                            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                collectionPosterProgressBar.visibility = View.GONE
                                return false
                            }

                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                collectionPosterProgressBar.visibility = View.GONE
                                return false
                            }
                        })
                        .apply(RequestOptions()
                                .error(R.drawable.poster_placeholder)
                                .centerCrop())
                        .into(collectionPoster)
                Glide.with(this@CollectionFragment)
                        .asBitmap()
                        .load(bannerUri)
                        .apply(RequestOptions().transforms(BlurTransformation(25)))
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    collectionRoot.background = BitmapDrawable(resource)
                                }
                                val palette = Palette.from(resource).generate()
                                try {
                                    mView.findViewById<TextView>(R.id.collectionName).setTextColor(palette.vibrantSwatch?.rgb!!)
                                    mView.findViewById<TextView>(R.id.collectionOverview).setTextColor(palette.darkMutedSwatch?.rgb!!)
                                } catch (ex: Exception) {

                                }
                            }
                        })

                collectionName.text = collection?.name
                collectionOverview.text = collection?.overview
                collectionMovies.clear()
                for (item in collection!!.parts) collectionMovies.add(item)
                viewAdapter.notifyDataSetChanged()
                isLoading = false

            }
        })
    }

    private fun callCollectionMovie(): Call<Collection> {
        val call = client.getCollection(
                collectionId.toInt(),
                BuildConfig.TmdbApiKey
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