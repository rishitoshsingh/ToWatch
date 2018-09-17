package com.alphae.rishi.towatch.Activities

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.alphae.rishi.towatch.Adapters.CompaniesAdapter
import com.alphae.rishi.towatch.Adapters.SlidingImageAdapter
import com.alphae.rishi.towatch.Api.ServiceGenerator
import com.alphae.rishi.towatch.BuildConfig
import com.alphae.rishi.towatch.Database.WatchDatabase
import com.alphae.rishi.towatch.Database.WatchList
import com.alphae.rishi.towatch.Database.WatchedList
import com.alphae.rishi.towatch.Fragments.CollectionFragment
import com.alphae.rishi.towatch.Fragments.RecommendationFragment
import com.alphae.rishi.towatch.Fragments.SimilarFragment
import com.alphae.rishi.towatch.Listners.AppBarStateChangeListener
import com.alphae.rishi.towatch.POJOs.ExternalIds
import com.alphae.rishi.towatch.POJOs.TmdbMovie.Details
import com.alphae.rishi.towatch.POJOs.TmdbMovie.MovieImage
import com.alphae.rishi.towatch.POJOs.TmdbMovie.VideoResults
import com.alphae.rishi.towatch.R
import com.alphae.rishi.towatch.TmdbApi.TmdbApiClient
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.facebook.ads.*
import com.google.android.gms.ads.AdListener
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerFragment
import kotlinx.android.synthetic.main.activity_movie_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Time
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
    private var presentInWatchedList: Boolean = false
    private lateinit var youTubePlayerFragment: YouTubePlayerFragment
    private var youTubePlayer: YouTubePlayer? = null
    private var VideoResult: List<com.alphae.rishi.towatch.POJOs.TmdbMovie.Result>? = null
    private var currentVideo: Int = 0

    private lateinit var collectionFragment: CollectionFragment

    private lateinit var externalIds: ExternalIds

    lateinit var mPalette: Palette
    private lateinit var nativeAd: NativeAd
    private lateinit var recommendationFragment: RecommendationFragment

    private lateinit var similarFragment: SimilarFragment
    private lateinit var mInterstitialAd: com.facebook.ads.InterstitialAd

    @SuppressLint("RestrictedApi")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)
        mInterstitialAd = InterstitialAd(this, BuildConfig.FanInterstitial)
        mInterstitialAd.setAdListener(object : InterstitialAdListener {
            override fun onInterstitialDisplayed(p0: Ad?) {}
            override fun onAdClicked(p0: Ad?) {}
            override fun onInterstitialDismissed(p0: Ad?) {}
            override fun onError(p0: Ad?, p1: AdError?) {}
            override fun onAdLoaded(p0: Ad?) {
                val sharedPreferences: SharedPreferences = getSharedPreferences("Notification", Context.MODE_PRIVATE)
                if (sharedPreferences.getBoolean("Clicked", false)) {
                    mInterstitialAd.show()
                    val sharedPreferenceEditor: SharedPreferences.Editor = sharedPreferences.edit()
                    sharedPreferenceEditor.putBoolean("Clicked", false)
                    sharedPreferenceEditor.commit()
                }
            }
            override fun onLoggingImpression(p0: Ad?) {}
        })
        mInterstitialAd.loadAd()

        setSupportActionBar(toolbar)
        mToolbar = supportActionBar
        mToolbar?.setDisplayHomeAsUpEnabled(true)

//        val window: Window = window
//        window.clearFlags(FLAG_TRANSLUCENT_STATUS)
//        window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        watchDatabase = WatchDatabase.getInstance(this)!!

        if (native_ad_container.childCount != 1) loadNativeAd()

        val packageName = "com.google.android.youtube"
        val isYoutubeInstalled = isAppInstalled(packageName)
        if (!isYoutubeInstalled) {
            Toast.makeText(this, "Install Youtube to watch Trailers", Toast.LENGTH_SHORT).show()
        }
        if (isYoutubeInstalled) {

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
        }

        app_bar_layout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                if (state == State.COLLAPSED && !appBarCollapsed) {
//                    download_fabSecond.show()
                    fabSecond.show()
                    appBarCollapsed = true
                } else if (state == State.EXPANDED && appBarCollapsed) {
//                    download_fabSecond.hide()
                    fabSecond.hide()
                    appBarCollapsed = false
                } else {
//                    download_fabSecond.hide()
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
        try {
            Glide.with(this)
                    .asBitmap()
                    .load(posterUri)
                    .listener(object : RequestListener<Bitmap> {
                        override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            posterProgressBar.visibility = View.GONE
                            mPalette = Palette.from(resource!!).generate()
                            try {
                                collasping_toolbar_child.setBackgroundColor(mPalette.darkMutedSwatch?.rgb!!)
                                collasping_toolbar_genre.setTextColor(mPalette.lightVibrantSwatch?.rgb!!)
                                tmdb_rating.setTextColor(mPalette.lightVibrantSwatch?.rgb!!)
                                imdb_rating.setTextColor(mPalette.lightVibrantSwatch?.rgb!!)
                                tagline_card_view.setCardBackgroundColor(mPalette.darkVibrantSwatch?.rgb!!)
                                overview_title.setTextColor(mPalette.darkVibrantSwatch?.rgb!!)
                                movie_tagline.setTextColor(mPalette.vibrantSwatch?.rgb!!)
                                previousVideoButton.backgroundTintList = ColorStateList.valueOf(mPalette.vibrantSwatch?.rgb!!)
                                nextVideoButton.backgroundTintList = ColorStateList.valueOf(mPalette.vibrantSwatch?.rgb!!)
                                fab.backgroundTintList = ColorStateList.valueOf(mPalette.vibrantSwatch?.rgb!!)
                                fabSecond.backgroundTintList = ColorStateList.valueOf(mPalette.vibrantSwatch?.rgb!!)
//                                download_fab.backgroundTintList = ColorStateList.valueOf(mPalette.vibrantSwatch?.rgb!!)
//                                download_fabSecond.backgroundTintList = ColorStateList.valueOf(mPalette.vibrantSwatch?.rgb!!)
                                collasping_toolbar.contentScrim = ColorDrawable(mPalette.darkMutedSwatch?.rgb!!)
                                collasping_toolbar.statusBarScrim = ColorDrawable(mPalette.darkMutedSwatch?.rgb!!)
                            } catch (ex: Exception) {

                            }
                            val bundle: Bundle = Bundle()
                            bundle.putLong("movieId", movieId)
                            try {
                                bundle.putInt("bgcolor", mPalette.darkMutedSwatch?.rgb!!)
                                bundle.putInt("accentColor", mPalette.vibrantSwatch?.rgb!!)
                            } catch (ex: Exception) {

                            }
                            recommendationFragment = RecommendationFragment()
                            similarFragment = SimilarFragment()
                            recommendationFragment.arguments = bundle
                            similarFragment.arguments = bundle
                            supportFragmentManager.beginTransaction()
                                    .replace(R.id.similarMoviesFrame, similarFragment)
                                    .disallowAddToBackStack()
                                    .commit()
                            supportFragmentManager.beginTransaction()
                                    .replace(R.id.recommendedMoviesFrame, recommendationFragment)
                                    .disallowAddToBackStack()
                                    .commit()
                            return false
                        }

                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                            posterProgressBar.visibility = View.GONE
                            return false
                        }
                    })
                    .apply(RequestOptions()
                            .centerCrop())
                    .into(movie_poster)


        } catch (ex: Exception) {
            Log.d("Saved", "Glide Crash")
            this.onDestroy()
        }
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

        website.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(movie.homepage)
            startActivity(intent)
        }

        facebook.setOnClickListener {
            val packageManager: PackageManager = this.packageManager
            try {
                val versionCode: Int = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
                if (versionCode >= 3002850) { //newer versions of fb app
                    val facebookUrl = "fb://facewebmodal/f?href=https://www.facebook.com/" + externalIds.facebookId
                    val facebookIntent = Intent(Intent.ACTION_VIEW)
                    facebookIntent.data = Uri.parse(facebookUrl)
                    startActivity(facebookIntent)
                } else { //older versions of fb app
                    val url = "fb://page/" + externalIds.facebookId
                    val facebookIntent = Intent(Intent.ACTION_VIEW)
                    facebookIntent.data = Uri.parse(url)
                    startActivity(facebookIntent)
                }
            } catch (ex: PackageManager.NameNotFoundException) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + externalIds.facebookId))
                startActivity(intent)
            }
        }

        instagram.setOnClickListener {
            val uri: Uri = Uri.parse("http://instagram.com/_u/" + externalIds.instagramId)
            val instagramIntent = Intent(Intent.ACTION_VIEW, uri)
            instagramIntent.`package` = "com.instagram.android"
            try {
                startActivity(instagramIntent)
            } catch (ex: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/" + externalIds.instagramId)))

            }
        }

        twitter.setOnClickListener {
            try {
                // get the Twitter app if possible
                this.packageManager.getPackageInfo("com.twitter.android", 0)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + externalIds.twitterId))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } catch (ex: Exception) {
                // no Twitter app, revert to browser
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + externalIds.twitterId))
                startActivity(intent)
            }
        }

        getMovieDetails()

    }

    private fun playVideo(videoId: String) {
        if (youTubePlayer != null) {
            try {
                youTubePlayer!!.cueVideo(videoId)
                youTubePlayer!!.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
                youTubePlayer!!.setOnFullscreenListener {
                    if (it) {
                        this@MovieDetailsActivity.onPause()
                    } else {
                        this@MovieDetailsActivity.onResume()
                    }

                }
            } catch (ex: Exception) {
            }
        }
    }

    private fun getMovieVideos() {

        val packageName = "com.google.android.youtube"
        val isYoutubeInstalled = isAppInstalled(packageName)
        if (!isYoutubeInstalled) {
            Toast.makeText(this, "Install Youtube to watch Trailers", Toast.LENGTH_SHORT).show()
            return
        }
        val call = callMovieVideos()
        call.enqueue(object : Callback<VideoResults> {
            override fun onFailure(call: Call<VideoResults>?, t: Throwable?) {
                Log.v("Video Network Call", "Retrofit", t)
            }

            override fun onResponse(call: Call<VideoResults>?, response: Response<VideoResults>?) {
                VideoResult = response?.body()?.results
                if (VideoResult?.isEmpty()!!) {
                    Toast.makeText(this@MovieDetailsActivity, "No Trailer Found", Toast.LENGTH_LONG).show()
                    previousVideoButton.isEnabled = false
                    nextVideoButton.isEnabled = false
                } else {
                    if (VideoResult!!.size == 1) {
                        previousVideoButton.visibility = View.GONE
                        nextVideoButton.visibility = View.GONE
                    }
                    playVideo(VideoResult?.get(0)?.key!!)
                }
            }
        })
    }

    private fun isAppInstalled(packageName: String): Boolean {
        val mIntent = packageManager.getLaunchIntentForPackage(packageName)
        return mIntent != null
    }

    private fun getExternalIds() {
        callExternalIds().enqueue(object : Callback<ExternalIds> {
            override fun onFailure(call: Call<ExternalIds>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ExternalIds>?, response: Response<ExternalIds>?) {
                externalIds = response?.body()!!
                if (externalIds.instagramId != null) instagram.visibility = View.VISIBLE
                if (externalIds.twitterId != null) twitter.visibility = View.VISIBLE
                if (externalIds.facebookId != null) facebook.visibility = View.VISIBLE
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
                val movieImages: MovieImage = response?.body()!!
                val backdropUrls: ArrayList<String> = ArrayList()
                for (image in movieImages.backdrops) {
                    backdropUrls.add(image.filePath)
                }
                if (backdropUrls.size != 0) {
                    backdropCard.visibility = View.GONE
                    backdropViewPager.visibility = View.VISIBLE
                    pagerIndicator.visibility = View.VISIBLE
                    backdropViewPager.adapter = SlidingImageAdapter(this@MovieDetailsActivity, backdropUrls)
                    pagerIndicator.attachToViewPager(backdropViewPager)
                }
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
                if (movie.belongsToCollection != null) {
                    val bundle: Bundle = Bundle()
                    bundle.putLong("collectionId", movie.belongsToCollection.id)
                    collectionFragment = CollectionFragment()
                    collectionFragment.arguments = bundle
                    try {
                        supportFragmentManager.beginTransaction()
                                .replace(R.id.collectionFrameLayout, collectionFragment)
                                .disallowAddToBackStack()
                                .commit()
                    } catch (ex: Exception) {

                    }
                    viewBelowFrame.visibility = View.VISIBLE

                }

                getMovieImages()
                getMovieVideos()
                getExternalIds()
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

    private fun callExternalIds(): Call<ExternalIds> {
        val call = client.getExternalIds(
                movieId.toInt(),
                BuildConfig.TmdbApiKey
        )
        return call
    }


    private fun updateMoviesDetails(details: Details?) {
        if (movie.homepage != null) website.visibility = View.VISIBLE
        mToolbar?.title = details?.originalTitle
        tmdb_rating.text = details?.voteAverage.toString()
        if (details?.tagline.isNullOrEmpty())
            movie_tagline.visibility = View.GONE
        else
            movie_tagline.text = details?.tagline
        status.text = details?.status
        release_date.text = formatDate(details?.releaseDate)
        revenue.text = formatRevenue(details?.revenue)
        runtime.text = formatRuntime(details?.runtime)
        movie_overview.text = details?.overview

        for (genre in details?.genres!!) {
            val textView = TextView(this)
            textView.text = genre.name
            textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            textView.setTextColor(resources.getColor(R.color.grey))
            try {
                textView.setTextColor(mPalette.lightMutedSwatch?.rgb!!)
            } catch (ex: Exception) {

            }
            textView.gravity = Gravity.CENTER
            movie_genre_list.addView(textView)
        }

        val logoUrls: ArrayList<String> = ArrayList()
        for (company in details.productionCompanies!!) {
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
        try {
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
        } catch (ex: Exception) {
            Log.d("Saved", "Glide Crash")
            this.onDestroy()
        }
        toolbar?.title = movie.title
        if (movie.title != movie.originalTitle) toolbar?.subtitle = movie.originalTitle

        FindWatchedMovie().execute(movie.id)
        fab.setOnClickListener {
            when {
                presentInWatchedList -> {
                    val data = WatchedList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                    RemoveWatchedMovie().execute(data)
                }
                presentInList -> {
                    val data = WatchList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                    RemoveMovie().execute(data)
                }
                else -> {
                    val data = WatchList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                    InsertMovie().execute(data)
                }
            }
        }

        fabSecond.setOnClickListener {
            when {
                presentInWatchedList -> {
                    val data = WatchedList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                    RemoveWatchedMovie().execute(data)
                }
                presentInList -> {
                    val data = WatchList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                    RemoveMovie().execute(data)
                }
                else -> {
                    val data = WatchList(movie.title, movie.id, movie.posterPath, movie.releaseDate)
                    InsertMovie().execute(data)
                }
            }
        }

//        download_fab.setOnClickListener {
//            val shareIntent: Intent = ShareCompat.IntentBuilder.from(this)
//                    .setType("text/plain")
//                    .setText(movie.imdbId)
//                    .intent
//            shareIntent.`package` = "com.alphae.rishi.yifyclient"
//            if (shareIntent.resolveActivity(packageManager) != null) {
//                startActivity(shareIntent)
//            }
//        }
//
//        download_fabSecond.setOnClickListener {
//            val shareIntent: Intent = ShareCompat.IntentBuilder.from(this)
//                    .setType("text/plain")
//                    .setText(movie.imdbId)
//                    .intent
//            shareIntent.`package` = "com.alphae.rishi.yifyclient"
//            if (shareIntent.resolveActivity(packageManager) != null) {
//                startActivity(shareIntent)
//            }
//        }

    }

    private fun formatRuntime(runtime: Long?): CharSequence? {
        try {
            val time = Time(0, runtime?.toInt()!!, 0)
            return time.toString()

        } catch (ex: Exception) {

        }
        return runtime.toString()
    }

    private fun formatDate(releaseDate: String?): CharSequence? {
        val items = releaseDate?.split("-")
        try {
            val calendar = Calendar.getInstance()
            calendar.set(items!![0].toInt(), items[1].toInt(), items[2].toInt())
            val date = calendar.time
            val DATE_FORMAT = SimpleDateFormat("dd-MM-yyyy")
            val dateString = DATE_FORMAT.format(date)
            return dateString
        } catch (ex: Exception) {

        }
        return releaseDate
    }

    private fun formatRevenue(revenue: Long?): String {
        try {
            val revenueFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)
            return revenueFormat.format(revenue).toString()
        } catch (ex: Exception) {

        }
        return "--"
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
        try {
            collectionFragment.onDestroy()
        } catch (ex: Exception) {
        }
        try {
            similarFragment.onDestroy()
        } catch (ex: Exception) {
        }
        try {
            recommendationFragment.onDestroy()
        } catch (ex: Exception) {
        }
        try {
            youTubePlayerFragment.onDestroy()
        } catch (ex: Exception) {
        }
        WatchDatabase.destroyInstance()
        super.onDestroy()
    }


    private inner class InsertMovie : AsyncTask<WatchList, Void, Void?>() {
        override fun doInBackground(vararg movieData: WatchList): Void? {
            val data = movieData[0]
            watchDatabase.watchDaoAccess().insertMovie(data)
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

    private inner class RemoveWatchedMovie : AsyncTask<WatchedList, Void, Void>() {
        override fun doInBackground(vararg params: WatchedList?): Void? {
            val movie = params[0]
            watchDatabase.watchedDaoAccess().deleteMovie(movie!!)
            return null
        }

        override fun onPostExecute(result: Void?) {
            presentInWatchedList = false
            Snackbar.make(movie_details_layout, "Removed from Watched List", Snackbar.LENGTH_SHORT).show()
            fab.setImageDrawable(resources.getDrawable(R.drawable.ic_playlist_add_white_48dp))
            fabSecond.setImageDrawable(resources.getDrawable(R.drawable.ic_playlist_add_white_48dp))
        }
    }

    private inner class FindWatchedMovie : AsyncTask<Long, Void, Boolean>() {
        override fun doInBackground(vararg params: Long?): Boolean {
            val movieId = params[0]
            val movieList = watchDatabase.watchedDaoAccess().fetchMovie(movieId!!)
            return !movieList.isEmpty()

        }

        override fun onPostExecute(result: Boolean) {
            if (result) {
                presentInWatchedList = true
                fab.setImageDrawable(resources.getDrawable(R.drawable.ic_visibility_white_48dp))
                fabSecond.setImageDrawable(resources.getDrawable(R.drawable.ic_visibility_white_48dp))
            } else {
                presentInWatchedList = false
                FindMovie().execute(movie.id)
            }
        }
    }


    private fun loadNativeAd() {

//        nativeAd = NativeAd(this, "YOUR_PLACEMENT_ID")
        try {
            nativeAd = NativeAd(this, BuildConfig.FanNativeDetails)
            nativeAd.setAdListener(object : NativeAdListener {
                override fun onMediaDownloaded(p0: Ad?) {}
                override fun onAdClicked(p0: Ad?) {}
                override fun onError(p0: Ad?, p1: AdError?) {}
                override fun onLoggingImpression(p0: Ad?) {}
                override fun onAdLoaded(p0: Ad?) {
                    if (nativeAd == null || nativeAd != p0) {
                        return
                    }
                    inflateAd(nativeAd)
                }
            })
            nativeAd.loadAd()
        } catch (ex: Exception) {
        }

    }

    private fun inflateAd(nativeAd: NativeAd) {

        nativeAd.unregisterView()

        // Add the Ad view into the ad container.
        val nativeAdContainer = findViewById<LinearLayout>(R.id.native_ad_container)
        val inflater: LayoutInflater = LayoutInflater.from(this)
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        val adView = inflater.inflate(R.layout.item_native_ad, nativeAdContainer, false)
        nativeAdContainer.addView(adView)

        val adImage = adView.findViewById<AdIconView>(R.id.adImage)
        val tvAdTitle = adView.findViewById<TextView>(R.id.tvAdTitle)
        val tvAdHeadline = adView.findViewById<TextView>(R.id.tvAdHeadline)
        val tvAdBody = adView.findViewById<TextView>(R.id.tvAdBody)
        val btnCTA = adView.findViewById<Button>(R.id.btnCTA)
        val adChoicesContainer = adView.findViewById<LinearLayout>(R.id.adChoicesContainer)
        val mediaView = adView.findViewById<MediaView>(R.id.mediaView)
        val socialContext = adView.findViewById<TextView>(R.id.social_context)
        val sponsored = adView.findViewById<TextView>(R.id.sponsored_label)
        tvAdTitle.text = nativeAd.advertiserName
        tvAdHeadline.text = nativeAd.adHeadline
        tvAdBody.text = nativeAd.adBodyText
        sponsored.text = nativeAd.sponsoredTranslation
        socialContext.text = nativeAd.adSocialContext
        nativeAd.registerViewForInteraction(
                adView,
                mediaView,
                adImage,
                Arrays.asList(btnCTA, mediaView, tvAdTitle))
        btnCTA.text = nativeAd.adCallToAction
        if (adChoicesContainer.childCount != 1) {
            val adChoicesView = AdChoicesView(this, nativeAd, true)
            adChoicesContainer.addView(adChoicesView)
        }

    }


}
