package com.example.rishi.towatch.Adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.rishi.towatch.Activities.MovieDetailsActivity
import com.example.rishi.towatch.POJOs.Tmdb.Result
import com.example.rishi.towatch.R
import java.util.*

/**
 * Created by rishi on 14/3/18.
 */
abstract class MovieAdapter(context: Context, moviesPassed: ArrayList<Result>) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {
    private val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500/"
    private val mContext = context
    private var smallPoster: Boolean = false

    constructor(context: Context, moviesPassed: ArrayList<Result>, small: Boolean) : this(context, moviesPassed) {
        smallPoster = small
    }

    var movies: ArrayList<Result> = moviesPassed

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView:View
        if (smallPoster) {
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.small_movie_card, parent, false)
        } else {
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_list_grid, parent, false)
        }
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
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                .into(holder.moviePoster)
        holder.itemLayout.setOnClickListener {
            val intent = Intent(mContext, MovieDetailsActivity::class.java)
//            intent.putExtra("movie", movies[position])
            intent.putExtra("movieId", movies[position].id)
            intent.putExtra("posterPath", movies[position].posterPath)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as Activity, holder.moviePoster as View, "moviePoster")
            mContext.startActivity(intent, options.toBundle())
        }

        holder.threeDotMenu.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val popup = PopupMenu(mContext, holder.threeDotMenu)
                popup.inflate(R.menu.card_menu)
                popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem?): Boolean {
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
                        return false
                    }

                })
                popup.show()
            }

        })

    }

    abstract fun addMovie(movie: Result)
    abstract fun removeMovie(movie: Result)
    abstract fun watchedMovie(movie: Result)


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

