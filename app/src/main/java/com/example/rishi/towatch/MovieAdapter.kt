package com.example.rishi.towatch

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.rishi.towatch.Activities.MovieDetailsActivity
import com.example.rishi.towatch.Activities.movies
import com.example.rishi.towatch.POJOs.TmdbDiscover.Result
import com.squareup.picasso.Picasso
import java.util.*

/**
 * Created by rishi on 14/3/18.
 */
class MovieAdapter(context: Context, movies: ArrayList<Result>) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {
    private val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500/"
    private val mContext = context

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.movie_list_grid, parent, false)
        return ViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        return movies.size

    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

        val movie = movies[position]
        val dateString = movie.releaseDate.split("-")
        val date = Date(dateString.get(0).toInt(),dateString.get(1).toInt(),dateString.get(2).toInt())
        val posterUri = Uri.parse(IMAGE_BASE_URL+movie.posterPath)
        holder?.movieTitleText?.text = movie.title
        holder?.movieReleaseDate?.text = date.year.toString()
        Picasso.with(mContext)
                .load(posterUri)
                .placeholder(R.drawable.poster_placeholder)
                .error(R.drawable.poster_placeholder)
                .into(holder?.moviePoster)
        holder?.itemLayout?.setOnClickListener {
            val intent = Intent(mContext, MovieDetailsActivity::class.java)
            intent.putExtra("movie", movies.get(position))
            mContext.startActivity(intent)
        }

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var movieTitleText: TextView
        var movieReleaseDate: TextView
        var moviePoster: ImageView
        var itemLayout:RelativeLayout

        init {
            movieTitleText = view.findViewById<TextView>(R.id.movieTile)
            movieReleaseDate = view.findViewById<TextView>(R.id.movieReleaseDate)
            moviePoster = view.findViewById<ImageView>(R.id.moviePoster)
            itemLayout = view.findViewById<RelativeLayout>(R.id.movieListGrid)
        }


    }

}

