package com.example.rishi.towatch.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.rishi.towatch.Adapters.CompaniesAdapter
import com.example.rishi.towatch.Api.ServiceGenerator
import com.example.rishi.towatch.BuildConfig
import com.example.rishi.towatch.Database.WatchDatabase
import com.example.rishi.towatch.Database.WatchList
import com.example.rishi.towatch.Fragments.CollectionFragment
import com.example.rishi.towatch.Fragments.RecommendationFragment
import com.example.rishi.towatch.Fragments.SimilarFragment
import com.example.rishi.towatch.Listners.AppBarStateChangeListener
import com.example.rishi.towatch.POJOs.TmdbMovie.Details
import com.example.rishi.towatch.POJOs.TmdbMovie.MovieImage
import com.example.rishi.towatch.POJOs.TmdbMovie.VideoResults
import com.example.rishi.towatch.R
import com.example.rishi.towatch.TmdbApi.TmdbApiClient
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerFragment
import kotlinx.android.synthetic.main.activity_movie_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Field


class MovieDetailsActivity : AppCompatActivity() {

    private val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500/"
    private val BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w1280/"
    private var mToolbar: ActionBar? = null
    private lateinit var movie: Details
    private lateinit var posterUri: Uri
    private lateinit var backdropUri: Uri
    private var movieId: Long = 0
    private lateinit var client: TmdbApiClient
    private var appBarCollapsed: Boolean = false
    private lateinit var watchDatabase: WatchDatabase
    private var presentInList: Boolean = false
    private lateinit var youTubePlayerFragment: YouTubePlayerFragment
    private var youTubePlayer: YouTubePlayer? = null
    private var VideoResult: List<com.example.rishi.towatch.POJOs.TmdbMovie.Result>? = null
    private var currentVideo: Int = 0

    @SuppressLint("RestrictedApi")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        setSupportActionBar(toolbar)
        mToolbar = supportActionBar
        mToolbar?.setDisplayHomeAsUpEnabled(true)

//        val window: Window = window
//        window.clearFlags(FLAG_TRANSLUCENT_STATUS)
//        window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)


        watchDatabase = WatchDatabase.getInstance(this)!!

        youTubePlayerFragment = fragmentManager.findFragmentById(R.id.youtubeFragment) as YouTubePlayerFragment
        youTubePlayerFragment.initialize(BuildConfig.YoutubeApiKey, object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, p1: YouTubePlayer?, p2: Boolean) {
                if (!p2) {
                    if (youTubePlayer == null) {
                        youTubePlayer = p1
                    }
                }
            }

            override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
                Toast.makeText(this@MovieDetailsActivity, p1.toString(), Toast.LENGTH_LONG).show()
            }
        })

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
//        movie = intent.getSerializableExtra("movie") as Result

        movieId = intent.getLongExtra("movieId", 0)

        posterUri = Uri.parse(POSTER_BASE_URL + intent.getStringExtra("posterPath"))
        Glide.with(this)
                .load(posterUri)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        posterProgressBar.visibility = View.GONE
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        posterProgressBar.visibility = View.GONE
                        return false
                    }
                })
                .apply(RequestOptions()
                        .centerCrop())
                .into(movie_poster)



        client = ServiceGenerator.createService(TmdbApiClient::class.java)



        previousVideoButton.setOnClickListener {
            if (currentVideo == 0) currentVideo = VideoResult?.size!! - 1 else currentVideo--
            val videoId: String = VideoResult?.get(currentVideo)!!.key
            playVideo(videoId)
        }

        nextVideoButton.setOnClickListener {
            if (currentVideo + 1 == VideoResult?.size) currentVideo = 0 else currentVideo++
            val videoId: String = VideoResult?.get(currentVideo)!!.key
            playVideo(videoId)
        }

        getMovieDetails()

        val bundle:Bundle = Bundle()
        bundle.putLong("movieId",movieId)
        val recommendationFragment = RecommendationFragment()
        val similarFragment = SimilarFragment()
        recommendationFragment.arguments = bundle
        similarFragment.arguments = bundle
        supportFragmentManager.beginTransaction()
                .replace(R.id.similarMoviesFrame,similarFragment)
                .disallowAddToBackStack()
                .commit()
        supportFragmentManager.beginTransaction()
                .replace(R.id.recommendedMoviesFrame,recommendationFragment)
                .disallowAddToBackStack()
                .commit()

    }

    private fun playVideo(videoId: String) {
        if (youTubePlayer != null) {
            youTubePlayer!!.cueVideo(videoId)
            youTubePlayer!!.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
        }
    }

    private fun getMovieVideos() {
        val call = callMovieVideos()
        call.enqueue(object : Callback<VideoResults> {
            override fun onFailure(call: Call<VideoResults>?, t: Throwable?) {
                Log.v("Video Network Call", "Retrofit", t)
            }

            override fun onResponse(call: Call<VideoResults>?, response: Response<VideoResults>?) {
                VideoResult = response?.body()?.results
                if (VideoResult?.isEmpty()!!) {
                    Toast.makeText(this@MovieDetailsActivity, "No Trailer Found", Toast.LENGTH_LONG).show()
                } else {
                    playVideo(VideoResult?.get(0)?.key!!)
                }
            }
        })
    }

    private fun callMovieVideos(): Call<VideoResults> {
        val call = client.getMovieVideos(
                movieId.toInt(),
                BuildConfig.TmdbApiKey,
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
                BuildConfig.TmdbApiKey
        )
        return call
    }

    private fun getMovieDetails() {
        val call = callMovieDetails()
        call.enqueue(object : Callback<Details> {
            override fun onResponse(call: Call<Details>?, response: Response<Details>?) {
                movie = response?.body()!!
                if (movie.belongsToCollection != null){
                    val bundle:Bundle = Bundle()
                    bundle.putLong("collectionId",movie.belongsToCollection.id)
                    val collectionFragment:CollectionFragment = CollectionFragment()
                    collectionFragment.arguments = bundle
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.collectionFrameLayout,collectionFragment)
                            .disallowAddToBackStack()
                            .commit()
                    viewBelowFrame.visibility = View.VISIBLE

                }

                getMovieImages()
                getMovieVideos()
//                UpdateUI()
                updateMoviesDetails(movie)
            }

            override fun onFailure(call: Call<Details>?, t: Throwable?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    private fun callMovieDetails(): Call<Details> {
        val call = client.getMovieDetails(
                movieId.toInt(),
                BuildConfig.TmdbApiKey,
                "en-US"
        )
        return call
    }


    private fun updateMoviesDetails(details: Details?) {
        mToolbar?.title = details?.originalTitle
        tmdb_rating.text = details?.voteAverage.toString()
        if(details?.tagline.isNullOrEmpty())
            movie_tagline.visibility = View.GONE
        else
            movie_tagline.text = details?.tagline
        status.text = details?.status
        release_date.text = details?.releaseDate
        revenue.text = details?.revenue.toString()
        runtime.text = details?.runtime.toString()
        movie_overview.text = details?.overview

        for (genre in details?.genres!!) {
            val textView = TextView(this)
            textView.text = genre.name
            textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            textView.setTextColor(resources.getColor(R.color.grey))
            textView.gravity = Gravity.CENTER
            movie_genre_list.addView(textView)
        }

        val logoUrls: ArrayList<String> = ArrayList()
        for (company in details?.productionCompanies!!) {
            if (company.logoPath == null) {
                continue
            }
            logoUrls.add(company.logoPath)
        }

        if (logoUrls.size != 0) {
            val adapter = CompaniesAdapter(this, logoUrls)
            production_company_grid.adapter = adapter
        } else {
            factsCompanyDivider.visibility = View.GONE
            companyLayout.visibility = View.GONE
        }

        backdropUri = Uri.parse(BACKDROP_BASE_URL + details.backdropPath)

        Glide.with(this)
                .load(backdropUri)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        backdropProgressBar.visibility = View.GONE
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        backdropProgressBar.visibility = View.GONE
                        return false
                    }
                })
                .apply(RequestOptions()
                        .centerCrop())
                .into(backdrop)

        toolbar?.title = movie.title
        if (movie.title != movie.originalTitle) toolbar?.subtitle = movie.originalTitle

        FindMovie().execute(movie.id)
        fab.setOnClickListener {
            if (!presentInList) {
                val data = WatchList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                InsertMovie().execute(data)
            } else {
                val data = WatchList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                RemoveMovie().execute(data)
            }
        }

        fabSecond.setOnClickListener {
            if (!presentInList) {
                val data = WatchList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                InsertMovie().execute(data)
            } else {
                val data = WatchList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                RemoveMovie().execute(data)
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                fab.visibility = View.GONE
                supportFinishAfterTransition()
                return true
            }
        }
        return false
    }

    override fun onDestroy() {
        WatchDatabase.destroyInstance()
        super.onDestroy()
    }

    private inner class InsertMovie : AsyncTask<WatchList, Void, Void?>() {
        override fun doInBackground(vararg movieData: WatchList): Void? {
            val data = movieData[0]
            watchDatabase.watchDaoAccess().insertMovie(data);
            return null
        }

        override fun onPostExecute(result: Void?) {
            presentInList = true
            Snackbar.make(movie_details_layout, "Added to Playlist", Snackbar.LENGTH_SHORT).show()
            fab.setImageDrawable(resources.getDrawable(R.drawable.ic_playlist_add_check_white_48dp))
            fabSecond.setImageDrawable(resources.getDrawable(R.drawable.ic_playlist_add_check_white_48dp))
        }
    }

    private inner class RemoveMovie : AsyncTask<WatchList, Void, Void>() {
        override fun doInBackground(vararg params: WatchList?): Void? {
            val movie = params[0]
            watchDatabase.watchDaoAccess().deleteMovie(movie!!)
            return null
        }

        override fun onPostExecute(result: Void?) {
            presentInList = false
            Snackbar.make(movie_details_layout, "Removed from Playlist", Snackbar.LENGTH_SHORT).show()
            fab.setImageDrawable(resources.getDrawable(R.drawable.ic_playlist_add_white_48dp))
            fabSecond.setImageDrawable(resources.getDrawable(R.drawable.ic_playlist_add_white_48dp))
        }
    }

    private inner class FindMovie : AsyncTask<Long, Void, Boolean>() {
        override fun doInBackground(vararg params: Long?): Boolean {
            val movieId = params[0]
            val movieList = watchDatabase.watchDaoAccess().fetchMovie(movieId!!)
            return !movieList.isEmpty()

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
