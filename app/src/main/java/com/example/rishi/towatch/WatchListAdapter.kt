package com.example.rishi.towatch

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.rishi.towatch.Activities.MovieDetailsActivity
import com.example.rishi.towatch.Database.WatchList
import com.example.rishi.towatch.POJOs.Tmdb.Result
import java.lang.Exception
import java.util.*

/**
 * Created by rishi on 8/5/18.
 */
class WatchListAdapter(context: Context, moviesPassed: List<WatchList>) : RecyclerView.Adapter<WatchListAdapter.WatchListViewHolder>() {
    private val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500/"
    private val mContext = context
    var movies: List<WatchList> = moviesPassed

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): WatchListViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.movie_list_grid, parent, false)
        return WatchListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: WatchListViewHolder?, position: Int) {
        val movie = movies[position]
        val dateString = movie.movieReleaseDate.split("-")
        val date = Date(dateString[0].toInt(), dateString[1].toInt(), dateString[2].toInt())
        val posterUri = Uri.parse(IMAGE_BASE_URL + movie.moviePoster)
        holder?.movieTitleText?.text = movie.movieName
        holder?.movieReleaseDate?.text = date.year.toString()
        Glide.with(mContext)
                .load(posterUri)
                .listener(object : RequestListener<Uri, GlideDrawable> {
                    override fun onException(e: Exception?, model: Uri?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
                        holder?.posterProgressBar?.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: GlideDrawable?, model: Uri?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                        holder?.posterProgressBar?.visibility = View.GONE
                        return false
                    }
                })
                .error(R.drawable.poster_placeholder)
                .centerCrop()
                .into(holder?.moviePoster!!)
//        holder.itemLayout.setOnClickListener {
//            val intent = Intent(mContext, MovieDetailsActivity::class.java)
//            intent.putExtra("movie", movies[position])
//            mContext.startActivity(intent)
        }


    inner class WatchListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var movieTitleText: TextView
        var movieReleaseDate: TextView
        var moviePoster: ImageView
        var itemLayout: RelativeLayout
        var posterProgressBar: ProgressBar

        init {
            movieTitleText = view.findViewById<TextView>(R.id.movieTile)
            movieReleaseDate = view.findViewById<TextView>(R.id.movieReleaseDate)
            moviePoster = view.findViewById<ImageView>(R.id.moviePoster)
            itemLayout = view.findViewById<RelativeLayout>(R.id.movieListGrid)
            posterProgressBar = view.findViewById<ProgressBar>(R.id.posterProgressBar)
        }
    }
}