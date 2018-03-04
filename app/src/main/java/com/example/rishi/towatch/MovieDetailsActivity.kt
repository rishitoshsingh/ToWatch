package com.example.rishi.towatch

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_movie_details.*

class MovieDetailsActivity : AppCompatActivity() {

    val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500/"
    val BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w1280/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)
        setSupportActionBar(toolbar)
        val toolbar = supportActionBar
        toolbar?.setDisplayHomeAsUpEnabled(true)




        val intent = getIntent()
        val movie:Movie = intent.getSerializableExtra("movie") as Movie
        val posterUri = Uri.parse(POSTER_BASE_URL+movie.getPosterPath())
        val backdropUri = Uri.parse(BACKDROP_BASE_URL+movie.getBackdropPath())

        Picasso.with(this)
                .load(posterUri)
                .placeholder(R.drawable.poster_placeholder)
                .into(movie_poster)
        Picasso.with(this)
                .load(backdropUri)
                .placeholder(R.drawable.poster_placeholder)
                .into(backdrop)

        toolbar?.setTitle(movie.getTitle())
        if(movie.getTitle()!=movie.getOriginalTitle()) toolbar?.setSubtitle(movie.getOriginalTitle())

        val genre:String = extractGenre(movie.getGenreIds())

        movie_genre.text = genre
        movie_overview.text = movie.getOverview()

    }

    private fun extractGenre(genreIds: ArrayList<Int>): String {
        var genre:String = ""
        if(genreIds.contains(28)) genre += "Action, "
        if(genreIds.contains(12)) genre += "Adventure, "
        if(genreIds.contains(16)) genre += "Animation, "
        if(genreIds.contains(35)) genre += "Comedy, "
        if(genreIds.contains(80)) genre += "Crime, "
        if(genreIds.contains(99)) genre += "Documentary, "
        if(genreIds.contains(18)) genre += "Drama, "
        if(genreIds.contains(10751)) genre += "Family, "
        if(genreIds.contains(14)) genre += "Fantasy, "
        if(genreIds.contains(36)) genre += "History, "
        if(genreIds.contains(27)) genre += "Horror, "
        if(genreIds.contains(80)) genre += "Crime, "
        if(genreIds.contains(10402)) genre += "Music, "
        if(genreIds.contains(9648)) genre += "Mystery, "
        if(genreIds.contains(10759)) genre += "Romance, "
        if(genreIds.contains(878)) genre += "Science Fiction, "
        if(genreIds.contains(10770)) genre += "TV Movie, "
        if(genreIds.contains(51)) genre += "Thriller, "
        if(genreIds.contains(10752)) genre += "War, "
        if(genreIds.contains(37)) genre += "Western, "

        genre.trim()
        genre = genre.substring(0,genre.length-2)

        return genre
    }
}
