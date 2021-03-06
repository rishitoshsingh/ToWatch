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
import com.alphae.rishi.towatch.Adapters.WatchedAdapter
import com.alphae.rishi.towatch.Database.WatchDatabase
import com.alphae.rishi.towatch.Database.WatchedList
import com.alphae.rishi.towatch.R
import kotlinx.android.synthetic.main.recycler_view.*


/**
 * A simple [Fragment] subclass.
 */
class WatchedListFragment : Fragment() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var watchedList: MutableList<WatchedList> = mutableListOf<WatchedList>()
    private lateinit var watchDatabase: WatchDatabase

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
        viewAdapter = object : WatchedAdapter(context!!, watchedList){
            override fun removeMovie(movie: WatchedList) {
                watchedList.remove(movie)
                if(refresh_layout != null){
                    refresh_layout.isRefreshing = true
                }
                RemoveMovie().execute(movie)
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
            watchedList.removeAll(watchedList)
            ReadFromDatabase().execute()
        }

    }

    override fun onDestroy() {
        WatchDatabase.destroyInstance()
        super.onDestroy()
    }

    private inner class RemoveMovie : AsyncTask<WatchedList, Void, Void>() {
        override fun doInBackground(vararg params: WatchedList?): Void? {
            val movie = params[0]
            watchDatabase.watchedDaoAccess().deleteMovie(movie!!)
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

    private inner class ReadFromDatabase : AsyncTask<Void, Void, List<WatchedList>>(){
        override fun doInBackground(vararg params: Void?): List<WatchedList> {
            return watchDatabase.watchedDaoAccess().fetchAllMovies()
        }

        override fun onPostExecute(result: List<WatchedList>?) {
            if(refresh_layout != null){
                refresh_layout.isRefreshing = false
            }
            shimmer_container.stopShimmerAnimation()
            shimmer_container.visibility = View.GONE
            for (item in result!!) watchedList.add(item)
            viewAdapter.notifyDataSetChanged()
        }
    }

}