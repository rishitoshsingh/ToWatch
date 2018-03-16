package com.example.rishi.towatch.Activities

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.rishi.towatch.POJOs.Tmdb.Result
import com.example.rishi.towatch.R
import kotlinx.android.synthetic.main.activity_movie_details.*
import java.lang.Exception

class MovieDetailsActivity : AppCompatActivity() {

    private val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500/"
    private val BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w1280/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        setSupportActionBar(toolbar)
        val toolbar = supportActionBar
        toolbar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        val movie: Result = intent.getSerializableExtra("movie") as Result
        val posterUri = Uri.parse(POSTER_BASE_URL + movie.posterPath)
        val backdropUri = Uri.parse(BACKDROP_BASE_URL + movie.backdropPath)
        Glide.with(this)
                .load(posterUri)
                .listener(object : RequestListener<Uri, GlideDrawable> {
                    override fun onException(e: Exception?, model: Uri?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
                        posterProgressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: GlideDrawable?, model: Uri?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                        posterProgressBar.visibility = View.GONE
                        return false
                    }

                })
                .centerCrop()
                .into(movie_poster)

        Glide.with(this)
                .load(backdropUri)
                .listener(object : RequestListener<Uri, GlideDrawable> {
                    override fun onException(e: Exception?, model: Uri?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
                        backdropProgressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: GlideDrawable?, model: Uri?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                        backdropProgressBar.visibility = View.GONE
                        return false
                    }

                })
                .centerCrop()
                .into(backdrop)

        toolbar?.title = movie.title
        if (movie.title != movie.originalTitle) toolbar?.subtitle = movie.originalTitle

        val genre: String = extractGenre(movie.getGenreIds())

        movie_genre.text = genre
        movie_overview.text = movie.getOverview()

        fab.setOnClickListener {
            Snackbar.make(movie_details_layout, "Added to Playlist", Snackbar.LENGTH_SHORT).show()
        }


    }

    private fun extractGenre(genreIds: List<Long>): String {
        var genre = ""
        if (genreIds.contains(28)) genre += "Action, "
        if (genreIds.contains(12)) genre += "Adventure, "
        if (genreIds.contains(16)) genre += "Animation, "
        if (genreIds.contains(35)) genre += "Comedy, "
        if (genreIds.contains(80)) genre += "Crime, "
        if (genreIds.contains(99)) genre += "Documentary, "
        if (genreIds.contains(18)) genre += "Drama, "
        if (genreIds.contains(10751)) genre += "Family, "
        if (genreIds.contains(14)) genre += "Fantasy, "
        if (genreIds.contains(36)) genre += "History, "
        if (genreIds.contains(27)) genre += "Horror, "
        if (genreIds.contains(80)) genre += "Crime, "
        if (genreIds.contains(10402)) genre += "Music, "
        if (genreIds.contains(9648)) genre += "Mystery, "
        if (genreIds.contains(10759)) genre += "Romance, "
        if (genreIds.contains(878)) genre += "Science Fiction, "
        if (genreIds.contains(10770)) genre += "TV Movie, "
        if (genreIds.contains(51)) genre += "Thriller, "
        if (genreIds.contains(10752)) genre += "War, "
        if (genreIds.contains(37)) genre += "Western, "

        genre.trim()
        genre = genre.substring(0, genre.length - 2)

        return genre
    }
}
