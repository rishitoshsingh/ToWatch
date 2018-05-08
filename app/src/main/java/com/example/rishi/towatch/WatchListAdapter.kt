package com.example.rishi.towatch

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.rishi.towatch.Database.WatchList
import java.lang.Exception
import java.util.*

/**
 * Created by rishi on 8/5/18.
 */
abstract class WatchListAdapter(context: Context, moviesPassed: List<WatchList>) : RecyclerView.Adapter<WatchListAdapter.WatchListViewHolder>() {
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
        holder.threeDotMenu.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val popup = PopupMenu(mContext, holder.threeDotMenu)
                popup.inflate(R.menu.watch_list_card_menu)
                popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem?): Boolean {
                        when (item!!.itemId) {
                            R.id.removeMovie -> {
                                removeMovie(movie)
                            }
                        }
                        return false
                    }

                })
                popup.show()
            }

        })


    }

    abstract fun removeMovie(movie: WatchList)


    inner class WatchListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

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