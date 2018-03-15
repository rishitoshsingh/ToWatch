package com.example.rishi.towatch

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.rishi.towatch.POJOs.TmdbDiscover.Result
import com.squareup.picasso.Picasso
import java.util.*

/**
 * Created by rishi on 13/3/18.
 */
class CustomAdapter(context: Context, movies: ArrayList<Result>) : ArrayAdapter<Result>(context, 0, movies) {


    private val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500/"

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var listItemView: View? = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.movie_list_grid,parent,false)
        }

        val movieTitleText = listItemView?.findViewById<TextView>(R.id.movieTile)
        val movieReleaseDate = listItemView?.findViewById<TextView>(R.id.movieReleaseDate)
        val moviePoster = listItemView?.findViewById<ImageView>(R.id.moviePoster)

        val currentMovie = getItem(position)

        val dateString = currentMovie.releaseDate?.split("-")
        val date = Date(dateString?.get(0)?.toInt()!!,dateString[1].toInt(),dateString[2].toInt())

        val posterUri = Uri.parse(IMAGE_BASE_URL+currentMovie.posterPath)

        Picasso.with(context)
                .load(posterUri)
                .placeholder(R.drawable.poster_placeholder)
                .into(moviePoster)

        movieTitleText?.text = currentMovie.title
        movieReleaseDate?.text = date.year.toString()

        return listItemView
    }
}