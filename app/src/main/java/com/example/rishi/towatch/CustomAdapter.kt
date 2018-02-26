package com.example.rishi.towatch

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

/**
 * Created by rishi on 25/2/18.
 */
class CustomAdapter(context: Context, movies: ArrayList<Movie>) : ArrayAdapter<Movie>(context, 0, movies) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var listItemView: View? = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.movie_list_grid,parent,false)
        }

        val movieTitleText = listItemView?.findViewById<TextView>(R.id.movieTile)
        val movieReleaseDate = listItemView?.findViewById<TextView>(R.id.movieReleaseDate)

        val currentMovie = getItem(position)

        movieTitleText?.text = currentMovie.getTitle()
        movieReleaseDate?.text = currentMovie.getReleaseDate()

        return listItemView
    }
}