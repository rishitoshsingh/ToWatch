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
import com.alphae.rishi.towatch.Database.WatchList
import com.alphae.rishi.towatch.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import java.util.*

/**
 * Created by rishi on 8/5/18.
 */
abstract class ListAdapter(context: Context, moviesPassed: List<WatchList>) : RecyclerView.Adapter<ListAdapter.MovieViewHolder>() {
    private val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500/"
    private val mContext = context
    var movies: List<WatchList> = moviesPassed

    private lateinit var mInterstitialAd: InterstitialAd

    init {
        mInterstitialAd = InterstitialAd(mContext)
//        mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        mInterstitialAd.adUnitId = BuildConfig.AdmobInterstitial
        mInterstitialAd.loadAd(AdRequest.Builder().build())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_list_grid, parent, false)
        return MovieViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        val dateString = movie.movieReleaseDate.split("-")
        var date:Date = Date(1971,2,1)
        try {
            date = Date(dateString[0].toInt(), dateString[1].toInt(), dateString[2].toInt())
        } catch (ex:Exception){
            Log.d("EX",movie.movieName)
            Log.d("EX",movie.movieReleaseDate)
            Log.d("EX",movie.movieId.toString())
        }
        val posterUri = Uri.parse(IMAGE_BASE_URL + movie.moviePoster)
        holder.movieTitleText.text = movie.movieName
        holder.movieReleaseDate.text = date.year.toString()
        Glide.with(mContext)
                .load(posterUri)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        holder.posterProgressBar.visibility = View.GONE
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        holder.posterProgressBar.visibility = View.GONE
                        return false
                    }
                })
                .apply(RequestOptions()
                        .centerCrop()
                        .error(R.drawable.poster_placeholder))
                .into(holder.moviePoster)
//        holder.itemLayout.setOnClickListener {
//            val intent = Intent(mContext, MovieDetailsActivity::class.java)
//            intent.putExtra("movie", movies[position])
//            mContext.startActivity(intent)
        holder.itemLayout.setOnClickListener {

            val sharedPreferences = mContext.getSharedPreferences("Interstitial", Context.MODE_PRIVATE)
            val userClicks = sharedPreferences.getInt("KeyEvents", 2)
            var showAd: Boolean = userClicks == 2

            mInterstitialAd.adListener = object : AdListener() {
                override fun onAdClosed() {
                    mInterstitialAd.loadAd(AdRequest.Builder().build())
                    transition(holder.adapterPosition, holder)
                }
            }
            if (userClicks != 2) {
                val shaPrefEditor = sharedPreferences.edit()
                shaPrefEditor.putInt("KeyEvents", userClicks + 1)
                shaPrefEditor.commit()
            }
            if (mInterstitialAd.isLoaded and showAd) {
                mInterstitialAd.show()
                val prefEditor = sharedPreferences.edit()
                prefEditor.putInt("KeyEvents", 0)
                prefEditor.commit()
                Log.d("Interstitial", "Interstitial shown")
            } else {
                if (showAd) {
                    val prefEditor = sharedPreferences.edit()
                    prefEditor.putInt("KeyEvents", 2)
                    prefEditor.commit()
                }
                Log.d("Interstitial", "The interstitial wasn't loaded yet.")
                transition(holder.adapterPosition, holder)
            }
        }
        holder.threeDotMenu.setOnClickListener {
            val popup = PopupMenu(mContext, holder.threeDotMenu)
            popup.inflate(R.menu.watch_list_card_menu)
            popup.setOnMenuItemClickListener { item ->
                when (item!!.itemId) {
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


    }

    private fun transition(position: Int,holder: MovieViewHolder){
        val intent = Intent(mContext, MovieDetailsActivity::class.java)
        intent.putExtra("movieId", movies[position].movieId)
        intent.putExtra("posterPath",movies[position].moviePoster)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as Activity,holder.moviePoster as View, "moviePoster")
        mContext.startActivity(intent,options.toBundle())

    }

    abstract fun removeMovie(movie: WatchList)
    abstract fun watchedMovie(movie: WatchList)

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
}