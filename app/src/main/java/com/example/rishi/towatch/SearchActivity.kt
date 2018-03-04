package com.example.rishi.towatch

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_search.*

var searchView: EditText? = null
var mSearchAdapter: CustomAdapter? = null


class SearchActivity : AppCompatActivity() {
    val YOUTUBE_API_KEY = "AIzaSyB17fukV4yjmWIizZ-Gei9wi51AICGov1g"
    val YOUTUBE_BASE_URL = "https://www.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&id="

    val TMDB_SEARCH_BASE_URL = "https://api.themoviedb.org/3/search/movie?api_key="
    val TMDB_KEY = "cc4b67c52acb514bdf4931f7cedfd12b"
    val LANGUAGE = "en-US"
    val PAGE = 1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(my_search_toolbar)


        searchView = searchMovie


        tempSearch.setOnQueryTextFocusChangeListener { v, hasFocus ->

            val query = createQuerystring(tempSearch.query.toString())
            val URL = TMDB_SEARCH_BASE_URL+TMDB_KEY+"&language="+LANGUAGE+"&query="+query+"&page="+PAGE.toString()+"&include_adult=true"
            mSearchAdapter = CustomAdapter(this, ArrayList())
            searchGridView.adapter = mSearchAdapter
            movieSearchAsyncTask().execute(URL)
        }


        val extras = intent.extras
        val value1 = extras!!.getString(Intent.EXTRA_TEXT)
        var videoId = value1.substringAfter("https://youtu.be/")
        val API_URL = YOUTUBE_BASE_URL + videoId + "&key=" + YOUTUBE_API_KEY

        YoutubeAsyncTask().execute(API_URL)
        search.setOnClickListener {
            val query = createQuerystring(searchMovie.text.toString())
            val URL = TMDB_SEARCH_BASE_URL+TMDB_KEY+"&language="+LANGUAGE+"&query="+query+"&page="+PAGE.toString()+"&include_adult=true"
            mSearchAdapter = CustomAdapter(this, ArrayList())
            searchGridView.adapter = mSearchAdapter
            movieSearchAsyncTask().execute(URL)
        }

    }




    fun createQuerystring(string: String?): String {
        var query:String =""
        if (string == null) {
            return "sex"
        } else {
            val strings = string.split(" ")
            for (str in strings){
                query+=str
                query+="%20"
            }
        }
        return query
    }

    class YoutubeAsyncTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            return QueryYoutube().getYoutubeResponse(params[0])
        }
        override fun onPostExecute(result: String?) {

            searchView?.setText(result, TextView.BufferType.EDITABLE)
            Log.i("Async task ", "Data Received")
        }
    }

    class movieSearchAsyncTask : AsyncTask<String, Void, ArrayList<Movie>>() {
        override fun doInBackground(vararg params: String?): ArrayList<Movie> {
            return QueryMovie().getMovieResponse(params[0])
        }

        override fun onPostExecute(result: ArrayList<Movie>?) {
            mSearchAdapter?.clear()
            if(result!=null&&!result.isEmpty())     mSearchAdapter?.addAll(result)
            Log.v("Search Movie","Data Received")
        }
    }

}
