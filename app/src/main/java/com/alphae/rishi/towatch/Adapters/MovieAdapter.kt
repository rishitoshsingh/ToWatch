package com.alphae.rishi.towatch.Adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.alphae.rishi.towatch.Activities.MovieDetailsActivity
import com.alphae.rishi.towatch.BuildConfig
import com.alphae.rishi.towatch.POJOs.Tmdb.Result
import com.alphae.rishi.towatch.R
import com.facebook.ads.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import java.util.*

/**
 * Created by rishi on 14/3/18.
 */
abstract class MovieAdapter(context: Context, moviesPassed: ArrayList<kotlin.Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500/"
    private val mContext = context
    private var smallPoster: Boolean = false
    var movies: ArrayList<kotlin.Any> = moviesPassed

    val MOVIE = 0
    val NATIVE_AD = 1

    constructor(context: Context, moviesPassed: ArrayList<kotlin.Any>, small: Boolean) : this(context, moviesPassed) {
        smallPoster = small
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var nullView: View = LayoutInflater.from(parent.context).inflate(R.layout.small_movie_card, parent, false)
        if (viewType == MOVIE) {
            var movieView: View
            if (smallPoster) {
                movieView = LayoutInflater.from(parent.context).inflate(R.layout.small_movie_card, parent, false)
            } else {
                movieView = LayoutInflater.from(parent.context).inflate(R.layout.movie_list_grid, parent, false)
            }
            return MovieViewHolder(movieView)
        } else if (viewType == NATIVE_AD) {
            val nativeAdItem = LayoutInflater.from(parent.context).inflate(R.layout.item_native_ad, parent, false)
            return NativeAdViewHolder(nativeAdItem)
        }
        return MovieViewHolder(nullView)
    }

    override fun getItemViewType(position: Int): Int {
        val item = movies.get(position)
        return if (item is Result) {
            MOVIE
        } else if (item is Ad) {
            NATIVE_AD
        } else {
            -1
        }
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemType = getItemViewType(position)

        if (itemType == MOVIE) {
            val movieViewHolder = holder as MovieViewHolder
            val movie = movies[position] as Result
            val dateString = movie.releaseDate.split("-")
            var date: Date = Date(1912, 1, 1)
            try {
                date = Date(dateString[0].toInt(), dateString[1].toInt(), dateString[2].toInt())
            } catch (ex: NumberFormatException) {

            }
            val posterUri = Uri.parse(IMAGE_BASE_URL + movie.posterPath)
            movieViewHolder.movieTitleText.text = movie.title
            movieViewHolder.movieReleaseDate.text = date.year.toString()
            Glide.with(mContext)
                    .load(posterUri)
                    .listener(object : RequestListener<Drawable> {
                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            movieViewHolder.posterProgressBar.visibility = View.GONE
                            return false
                        }

                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            movieViewHolder.posterProgressBar.visibility = View.GONE
                            return false
                        }
                    })
                    .apply(RequestOptions()
                            .error(R.drawable.poster_placeholder)
                            .centerCrop())
                    .into(movieViewHolder.moviePoster)
            movieViewHolder.itemLayout.setOnClickListener {

                val sharedPreferences = mContext.getSharedPreferences("Interstitial", Context.MODE_PRIVATE)
                val userClicks = sharedPreferences.getInt("KeyEvents", 2)
                var showAd: Boolean = userClicks == 2

                if (userClicks != 2) {
                    val shaPrefEditor = sharedPreferences.edit()
                    shaPrefEditor.putInt("KeyEvents", userClicks + 1)
                    shaPrefEditor.commit()
                    transition(holder.adapterPosition, holder)
                }

                if (showAd){
                    val interstitialPreferences = mContext.getSharedPreferences("TwoClicked", Context.MODE_PRIVATE)
                    val editor = interstitialPreferences.edit()
                    editor.putBoolean("TwoClicked",true)
                    editor.commit()
                    val prefEditor = sharedPreferences.edit()
                    prefEditor.putInt("KeyEvents", 0)
                    prefEditor.commit()
                    transition(holder.adapterPosition, holder)
                }
            }

            movieViewHolder.threeDotMenu.setOnClickListener {
                val popup = PopupMenu(mContext, holder.threeDotMenu)
                popup.inflate(R.menu.card_menu)
                popup.setOnMenuItemClickListener { item ->
                    when (item!!.itemId) {
                        R.id.addMovie -> {
                            addMovie(movie)
                        }
                        R.id.removeMovie -> {
                            removeMovie(movie)
                        }
                        R.id.watchedMovie -> {
                            watchedMovie(movie)
                        }
                    }
                    false
                }
                popup.show()
            }
        } else if (itemType == NATIVE_AD) {
            val nativeAdViewHolder = holder as NativeAdViewHolder
            val nativeAd = movies[position] as NativeAd

            val adImage = nativeAdViewHolder.adImage
            val tvAdTitle = nativeAdViewHolder.tvAdTitle
            val tvAdHeadline = nativeAdViewHolder.tvAdHeadline
            val tvAdBody = nativeAdViewHolder.tvAdBody
            val btnCTA = nativeAdViewHolder.btnCTA
            val adChoicesContainer = nativeAdViewHolder.adChoicesContainer
            val mediaView = nativeAdViewHolder.mediaView
            val socialContext = nativeAdViewHolder.tvAdSocialContext
            val sponsored = nativeAdViewHolder.tvSponsored
            tvAdTitle.text = nativeAd.advertiserName
            tvAdHeadline.text = nativeAd.adHeadline
            tvAdBody.text = nativeAd.adBodyText
            sponsored.text = nativeAd.sponsoredTranslation
            socialContext.text = nativeAd.adSocialContext
            nativeAd.registerViewForInteraction(
                    nativeAdViewHolder.container,
                    mediaView,
                    adImage,
                    Arrays.asList(btnCTA, mediaView))
            btnCTA.text = nativeAd.adCallToAction
            if (adChoicesContainer.childCount != 1) {
                val adChoicesView = AdChoicesView(mContext, nativeAd, true)
                adChoicesContainer.addView(adChoicesView)
            }
        }
    }


    private fun transition(position: Int, holder: MovieViewHolder) {
        val movie = movies[position] as Result
        val intent = Intent(mContext, MovieDetailsActivity::class.java)
        intent.putExtra("movieId", movie.id)
        intent.putExtra("posterPath", movie.posterPath)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as Activity, holder.moviePoster as View, "moviePoster")
        mContext.startActivity(intent, options.toBundle())
    }

    abstract fun addMovie(movie: Result)
    abstract fun removeMovie(movie: Result)
    abstract fun watchedMovie(movie: Result)


    inner class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var movieTitleText: TextView
        var movieReleaseDate: TextView
        var moviePoster: ImageView
        var itemLayout: RelativeLayout
        var posterProgressBar: ProgressBar
        var threeDotMenu: ImageView

        init {
            movieTitleText = view.findViewById<TextView>(R.id.movieTile)
            movieReleaseDate = view.findViewById<TextView>(R.id.movieReleaseDate)
            moviePoster = view.findViewById<ImageView>(R.id.moviePoster)
            itemLayout = view.findViewById<RelativeLayout>(R.id.movieListGrid)
            posterProgressBar = view.findViewById<ProgressBar>(R.id.posterProgressBar)
            threeDotMenu = view.findViewById<ImageView>(R.id.threeDotMenu)
        }
    }

    private class NativeAdViewHolder internal constructor(internal var container: View) : RecyclerView.ViewHolder(container) {
        internal var adImage: AdIconView
        internal var tvAdTitle: TextView
        internal var tvAdHeadline: TextView
        internal var tvAdBody: TextView
        internal var tvAdSocialContext: TextView
        internal var tvSponsored: TextView
        internal var btnCTA: Button
        internal var adChoicesContainer: LinearLayout
        internal var mediaView: MediaView

        init {
            adImage = container.findViewById(R.id.adImage) as AdIconView
            tvAdTitle = container.findViewById(R.id.tvAdTitle) as TextView
            tvAdHeadline = container.findViewById(R.id.tvAdHeadline) as TextView
            tvAdBody = container.findViewById(R.id.tvAdBody) as TextView
            tvAdSocialContext = container.findViewById(R.id.social_context) as TextView
            tvSponsored = container.findViewById(R.id.sponsored_label) as TextView
            btnCTA = container.findViewById(R.id.btnCTA) as Button
            adChoicesContainer = container.findViewById(R.id.adChoicesContainer) as LinearLayout
            mediaView = container.findViewById(R.id.mediaView) as MediaView
        }
    }


}

