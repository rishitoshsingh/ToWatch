package com.example.rishi.towatch.Activities

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Window
import android.view.WindowManager
import com.example.rishi.towatch.Database.WatchDatabase
import com.example.rishi.towatch.Database.WatchList
import com.example.rishi.towatch.R
import com.example.rishi.towatch.WatchListAdapter
import com.example.rishi.towatch.firebase.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_watch_list.*


class WatchListActivity : AppCompatActivity() {
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var watchList: MutableList<WatchList> = mutableListOf<WatchList>()
    private lateinit var watchDatabase: WatchDatabase

    private var mAuth: FirebaseAuth? = null

    override fun onStart() {
        val currentUser = mAuth?.currentUser
        super.onStart()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_list)
        mAuth = FirebaseAuth.getInstance()

        if(mAuth?.currentUser == null){
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }


        setSupportActionBar(my_watch_list_toolbar)
        val toolbar = supportActionBar
        toolbar?.title = "Watch List"

        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)


        watchDatabase = WatchDatabase.getInstance(this)!!
        ReadFromDatabase().execute()
        viewManager = GridLayoutManager(this, 2)
        viewAdapter = WatchListAdapter(this, watchList)
        watchListRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            itemAnimator = DefaultItemAnimator()
        }

    }

    private inner class ReadFromDatabase : AsyncTask<Void,Void,List<WatchList>>(){
        override fun doInBackground(vararg params: Void?): List<WatchList> {
            return watchDatabase.daoAccess().fetchAllMovies()
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: List<WatchList>?) {
            for (item in result!!) watchList.add(item)
            viewAdapter.notifyDataSetChanged()
        }
    }

}
