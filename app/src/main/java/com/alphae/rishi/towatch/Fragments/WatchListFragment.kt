package com.alphae.rishi.towatch.Fragments

import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alphae.rishi.towatch.Adapters.ListAdapter
import com.alphae.rishi.towatch.Database.WatchDatabase
import com.alphae.rishi.towatch.Database.WatchList
import com.alphae.rishi.towatch.Database.WatchedList
import com.alphae.rishi.towatch.R
import kotlinx.android.synthetic.main.recycler_view.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [WatchListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class WatchListFragment : Fragment() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var watchList: MutableList<WatchList> = mutableListOf<WatchList>()
    private lateinit var watchDatabase: WatchDatabase
    private lateinit var data:WatchList
    private lateinit var watchedData:WatchedList


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.recycler_view, container, false)
    }

    override fun onResume() {
        shimmer_container.startShimmerAnimation()
        super.onResume()
    }

    override fun onPause() {
        shimmer_container.stopShimmerAnimation()
        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        watchDatabase = WatchDatabase.getInstance(context!!)!!
        viewManager = GridLayoutManager(context, 2)
        viewAdapter = object : ListAdapter(context!!, watchList){
            override fun removeMovie(movie: WatchList) {
                watchList.remove(movie)
                if(refresh_layout != null){
                    refresh_layout.isRefreshing = true
                }
                RemoveMovie().execute(movie)

            }
            override fun watchedMovie(movie: WatchList) {
                watchedData = WatchedList(movie.movieName,movie.movieId,movie.moviePoster,movie.movieReleaseDate)
                watchList.remove(movie)
                data = WatchList(movie.movieName,movie.movieId,movie.moviePoster,movie.movieReleaseDate)
                if(refresh_layout != null){
                    refresh_layout.isRefreshing = true
                }
                InsertWatchedMovie().execute(watchedData)
            }

        }

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            itemAnimator = DefaultItemAnimator()
        }
        if(refresh_layout != null){
            refresh_layout.isRefreshing = true
        }
        ReadFromDatabase().execute()


        refresh_layout.setOnRefreshListener {
            shimmer_container.startShimmerAnimation()
            shimmer_container.visibility = View.VISIBLE
            watchList.removeAll(watchList)
            ReadFromDatabase().execute()
        }

    }

    override fun onDestroy() {
        WatchDatabase.destroyInstance()
        super.onDestroy()
    }


    private inner class InsertWatchedMovie : AsyncTask<WatchedList, Void, Void>(){
        override fun doInBackground(vararg params: WatchedList?): Void? {
            val movie = params[0]
            watchDatabase.watchedDaoAccess().insertMovie(movie!!)
            return null
        }

        override fun onPostExecute(result: Void?) {
            if(refresh_layout != null){
                refresh_layout.isRefreshing = false
            }
            RemoveMovie().execute(data)
            Snackbar.make(recyclerView,"Added to Watched", Snackbar.LENGTH_SHORT).show()
        }
    }


    private inner class RemoveMovie : AsyncTask<WatchList, Void, Void>() {
        override fun doInBackground(vararg params: WatchList?): Void? {
            val movie = params[0]
            watchDatabase.watchDaoAccess().deleteMovie(movie!!)
            return null
        }

        override fun onPostExecute(result: Void?) {
            if(refresh_layout != null){
                refresh_layout.isRefreshing = false
            }
            Snackbar.make(recyclerView, "Removed from Playlist", Snackbar.LENGTH_SHORT).show()
            viewAdapter.notifyDataSetChanged()
        }
    }

    private inner class ReadFromDatabase : AsyncTask<Void, Void, List<WatchList>>(){
        override fun doInBackground(vararg params: Void?): List<WatchList> {
            return watchDatabase.watchDaoAccess().fetchAllMovies()
        }

        override fun onPostExecute(result: List<WatchList>?) {
            for (item in result!!) watchList.add(item)
            viewAdapter.notifyDataSetChanged()
            if(refresh_layout != null){
                refresh_layout.isRefreshing = false
            }
            shimmer_container.stopShimmerAnimation()
            shimmer_container.visibility = View.GONE
        }
    }
}