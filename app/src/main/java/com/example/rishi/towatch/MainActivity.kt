package com.example.rishi.towatch

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

val BASE_URL = "https://api.themoviedb.org/3/discover/movie?api_key="
val API_KEY = "cc4b67c52acb514bdf4931f7cedfd12b"
val PAGE = 1
val RELEASE_YEAR = 2018
val LANGUAGE = "en-US"
val API_URL = BASE_URL+API_KEY+"&language="+LANGUAGE+"&sort_by=popularity.desc&include_adult=true&include_video=true&page="+PAGE.toString()+"&primary_release_year="+RELEASE_YEAR.toString()
var mAdapter: CustomAdapter? = null

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAdapter = CustomAdapter(this, ArrayList())
        gridview.adapter = mAdapter
        movieAsyncTask().execute(API_URL)

    }


    class movieAsyncTask : AsyncTask<String, Void, ArrayList<Movie>>() {
        override fun doInBackground(vararg params: String?): ArrayList<Movie> {
            return QueryMovie().getMovieResponse(params[0])
        }

        override fun onPostExecute(result: ArrayList<Movie>?) {
            mAdapter?.clear()
            if(result!=null&&!result.isEmpty())     mAdapter?.addAll(result)
            Log.v("AsyncTask","Data Received")
        }
    }

}

