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
import com.alphae.rishi.towatch.POJOs.TmdbCollection.Part
import com.alphae.rishi.towatch.R
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAdListener
import java.util.*

/**
 * Created by rishi on 17/6/18.
 */
abstract class CollectionAdapter(context: Context, moviesPassed: ArrayList<Part>) : RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {
    private val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500/"
    private val mContext = context
    var movies: ArrayList<Part> = moviesPassed

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.small_movie_card, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        val dateString = movie.releaseDate.split("-")
        var date: Date = Date(1912, 1, 1)
        try {
            date = Date(dateString[0].toInt(), dateString[1].toInt(), dateString[2].toInt())
        } catch (ex: NumberFormatException) {

        }
        val posterUri = Uri.parse(IMAGE_BASE_URL + movie.posterPath)
        holder.movieTitleText.text = movie.title
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
                        .error(R.drawable.poster_placeholder)
                        .centerCrop())
                .into(holder.moviePoster)
        holder.itemLayout.setOnClickListener {

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

        holder.threeDotMenu.setOnClickListener {
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

    }

    private fun transition(position: Int, holder: ViewHolder) {
        val intent = Intent(mContext, MovieDetailsActivity::class.java)
        intent.putExtra("movieId", movies[position].id)
        intent.putExtra("posterPath", movies[position].posterPath)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as Activity, holder.moviePoster as View, "moviePoster")
        mContext.startActivity(intent, options.toBundle())
    }

    abstract fun addMovie(movie: Part)
    abstract fun removeMovie(movie: Part)
    abstract fun watchedMovie(movie: Part)


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

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
