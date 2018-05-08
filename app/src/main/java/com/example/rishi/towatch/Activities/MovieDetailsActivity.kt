package com.example.rishi.towatch.Activities

import android.annotation.SuppressLint
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.rishi.towatch.Api.ServiceGenerator
import com.example.rishi.towatch.AppBarStateChangeListener
import com.example.rishi.towatch.Database.WatchDatabase
import com.example.rishi.towatch.Database.WatchList
import com.example.rishi.towatch.POJOs.Tmdb.Result
import com.example.rishi.towatch.POJOs.TmdbMovie.Details
import com.example.rishi.towatch.POJOs.TmdbMovie.MovieImage
import com.example.rishi.towatch.POJOs.TmdbMovie.VideoResults
import com.example.rishi.towatch.R
import com.example.rishi.towatch.TmdbApi.TmdbApiClient
import kotlinx.android.synthetic.main.activity_movie_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class MovieDetailsActivity : AppCompatActivity() {

    private val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500/"
    private val BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w1280/"
    private var mToolbar: ActionBar? = null
    private lateinit var movie: Result
    private lateinit var posterUri: Uri
    private lateinit var backdropUri: Uri
    private var movieId: Long = 0
    private lateinit var client: TmdbApiClient
    private var appBarCollapsed: Boolean = false
    private lateinit var watchDatabase: WatchDatabase
    private var presentInList:Boolean = false

    data class MovieData(var movieName: String, var movieId: Long, var moviePoster: String, var movieReleaseDate: String)

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        setSupportActionBar(toolbar)
        mToolbar = supportActionBar
        mToolbar?.setDisplayHomeAsUpEnabled(true)

        watchDatabase = WatchDatabase.getInstance(this)!!

        app_bar_layout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                if (state == State.COLLAPSED && !appBarCollapsed) {
                    fabSecond.show()
                    appBarCollapsed = true
                } else if (state == State.EXPANDED && appBarCollapsed) {
                    fabSecond.hide()
                    appBarCollapsed = false
                } else {
                    fabSecond.hide()
                }
            }
        })

//        window.decorView.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        val intent = intent
        movie = intent.getSerializableExtra("movie") as Result
        posterUri = Uri.parse(POSTER_BASE_URL + movie.posterPath)
        backdropUri = Uri.parse(BACKDROP_BASE_URL + movie.backdropPath)
        movieId = movie.id
        client = ServiceGenerator.createService(TmdbApiClient::class.java)
        getMovieDetails()
        getMovieImages()
        getMovieVideos()

        UpdateUI()


    }


    private fun getMovieVideos() {
        val call = callMovieVideos()
        call.enqueue(object : Callback<VideoResults> {
            override fun onFailure(call: Call<VideoResults>?, t: Throwable?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<VideoResults>?, response: Response<VideoResults>?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })

    }

    private fun callMovieVideos(): Call<VideoResults> {
        val call = client.getMovieVideos(
                movieId.toInt(),
                resources.getString(R.string.tmdb_key),
                "en-US"
        )
        return call
    }

    private fun getMovieImages() {
        val call = callMovieImages()
        call.enqueue(object : Callback<MovieImage> {
            override fun onFailure(call: Call<MovieImage>?, t: Throwable?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<MovieImage>?, response: Response<MovieImage>?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    private fun callMovieImages(): Call<MovieImage> {
        val call = client.getMovieImages(
                movieId.toInt(),
                resources.getString(R.string.tmdb_key)
        )
        return call
    }

    private fun getMovieDetails() {
        val call = callMovieDetails()
        call.enqueue(object : Callback<Details> {
            override fun onResponse(call: Call<Details>?, response: Response<Details>?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onFailure(call: Call<Details>?, t: Throwable?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    private fun callMovieDetails(): Call<Details> {
        val call = client.getMovieDetails(
                movieId.toInt(),
                resources.getString(R.string.tmdb_key),
                "en-US"
        )
        return call
    }


    private fun UpdateUI() {
        Glide.with(this)
                .load(posterUri)
                .listener(object : RequestListener<Uri, GlideDrawable> {
                    override fun onException(e: Exception?, model: Uri?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
                        posterProgressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: GlideDrawable?, model: Uri?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                        posterProgressBar.visibility = View.GONE
                        return false
                    }

                })
                .centerCrop()
                .into(movie_poster)

        Glide.with(this)
                .load(backdropUri)
                .listener(object : RequestListener<Uri, GlideDrawable> {
                    override fun onException(e: Exception?, model: Uri?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
                        backdropProgressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: GlideDrawable?, model: Uri?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                        backdropProgressBar.visibility = View.GONE
                        return false
                    }

                })
                .centerCrop()
                .into(backdrop)

        toolbar?.title = movie.title
        if (movie.title != movie.originalTitle) toolbar?.subtitle = movie.originalTitle

        val genre: String = extractGenre(movie.genreIds)

        movie_genre.text = genre
        movie_overview.text = movie.overview


        FindMovie().execute(movie.id)
        fab.setOnClickListener {
            if(!presentInList){
                val data = MovieData(movie.originalTitle, movie.id, movie.posterPath, movie.releaseDate)
                InsertMovie().execute(data)
            }

        }

        fabSecond.setOnClickListener {
            if(!presentInList){
                val data = MovieData(movie.originalTitle, movie.id, movie.posterPath, movie.releaseDate)
                InsertMovie().execute(data)
            }
        }

    }

    private fun extractGenre(genreIds: List<Long>): String {
        var genre = ""
        if (genreIds.contains(28)) genre += "Action, "
        if (genreIds.contains(12)) genre += "Adventure, "
        if (genreIds.contains(16)) genre += "Animation, "
        if (genreIds.contains(35)) genre += "Comedy, "
        if (genreIds.contains(80)) genre += "Crime, "
        if (genreIds.contains(99)) genre += "Documentary, "
        if (genreIds.contains(18)) genre += "Drama, "
        if (genreIds.contains(10751)) genre += "Family, "
        if (genreIds.contains(14)) genre += "Fantasy, "
        if (genreIds.contains(36)) genre += "History, "
        if (genreIds.contains(27)) genre += "Horror, "
        if (genreIds.contains(10402)) genre += "Music, "
        if (genreIds.contains(9648)) genre += "Mystery, "
        if (genreIds.contains(10759)) genre += "Romance, "
        if (genreIds.contains(878)) genre += "Science Fiction, "
        if (genreIds.contains(10770)) genre += "TV Movie, "
        if (genreIds.contains(51)) genre += "Thriller, "
        if (genreIds.contains(10752)) genre += "War, "
        if (genreIds.contains(37)) genre += "Western, "

        genre.trim()
        genre = genre.substring(0, genre.length - 2)

        return genre
    }

    override fun onDestroy() {
        WatchDatabase.destroyInstance()
        super.onDestroy()
    }

    private inner class InsertMovie : AsyncTask<MovieData, Void, Void?>() {
        override fun doInBackground(vararg movieData: MovieData): Void? {
            val data = movieData[0]
            watchDatabase.daoAccess().insertMovie(WatchList(null, data.movieName, data.movieId, data.moviePoster, data.movieReleaseDate));
            return null
        }

        override fun onProgressUpdate(vararg progress: Void) {

        }

        override fun onPostExecute(result: Void?) {
            presentInList = true
            Snackbar.make(movie_details_layout, "Added to Playlist", Snackbar.LENGTH_SHORT).show()
            fab.setImageDrawable(resources.getDrawable(R.drawable.ic_playlist_add_check_white_48dp))
            fabSecond.setImageDrawable(resources.getDrawable(R.drawable.ic_playlist_add_check_white_48dp))
        }
    }

    private inner class FindMovie : AsyncTask<Long, Void, Boolean>() {
        override fun doInBackground(vararg params: Long?): Boolean {
            val movieId = params[0]
            val movieList = watchDatabase.daoAccess().fetchMovie(movieId!!)
            return !movieList.isEmpty()

        }


        override fun onProgressUpdate(vararg progress: Void) {

        }

        override fun onPostExecute(result: Boolean) {
            if (result) {
                presentInList = true
                fab.setImageDrawable(resources.getDrawable(R.drawable.ic_playlist_add_check_white_48dp))
                fabSecond.setImageDrawable(resources.getDrawable(R.drawable.ic_playlist_add_check_white_48dp))
            } else {
                presentInList = false
                fab.setImageDrawable(resources.getDrawable(R.drawable.ic_playlist_add_white_48dp))
                fabSecond.setImageDrawable(resources.getDrawable(R.drawable.ic_playlist_add_white_48dp))
            }
        }
    }


}
