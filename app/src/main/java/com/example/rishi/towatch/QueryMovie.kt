package com.example.rishi.towatch

import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset

/**
 * Created by rishi on 25/2/18.
 */
class QueryMovie {

    fun getMovieResponse(urlString: String?): ArrayList<Movie> {
        val url: URL? = createUrl(urlString)
        var jsonResponse: String = ""

        try {
            jsonResponse = makeHttpRequest(url)
        } catch (ex: IOException) {
            Log.v("getMovieResponse", "Error in making request", ex)
        }
        Log.i("SearchHttp",jsonResponse)
        return extractMoviesFromJson(jsonResponse)
    }

    private fun createUrl(urlString: String?): URL? {
        var url: URL? = null
        try {
            url = URL(urlString)
        } catch (ex: MalformedURLException) {
            Log.v("createUrl", "Error in creating Url", ex)
        }
        return url
    }

    private fun makeHttpRequest(url: URL?): String {
        var jsonResponse: String = ""
        if (url == null) return jsonResponse
        val httpConnection: HttpURLConnection
        val inputStream: InputStream
        try {
            httpConnection = url.openConnection() as HttpURLConnection
            httpConnection.requestMethod = "GET"
            httpConnection.readTimeout = 10000
            httpConnection.connectTimeout = 10000
            httpConnection.connect()

            if (httpConnection.responseCode == 200) {
                inputStream = httpConnection.inputStream
                jsonResponse = readFromStream(inputStream)
            } else {
                Log.v("makeHttpRequest", "Connection Failed with response code " + httpConnection.responseCode.toString())
            }
        } catch (ex: Exception) {
            Log.v("makeHttpRequest", "Exception", ex)
        }
        return  jsonResponse
    }

    private fun readFromStream(inputStream: InputStream?): String {
        val output:StringBuilder = StringBuilder()
        if(inputStream!=null){
            val inputStreamReader:InputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
            val bufferReader:BufferedReader = BufferedReader(inputStreamReader)
            var line = bufferReader.readLine()
            while(line != null){
                output.append(line)
                line = bufferReader.readLine()
            }
        }
        return  output.toString()
    }


    private fun extractMoviesFromJson(jsonString: String): ArrayList<Movie> {
        val movies = ArrayList<Movie>()

        try {
            val root = JSONObject(jsonString)
            val results = root.getJSONArray("results")
            for (index in 0 until results.length()) {
                val movieObject = results.getJSONObject(index)
                val voteCount = movieObject.getInt("vote_count")
                val imdbId = movieObject.getInt("id")
                val video = movieObject.getBoolean("video")
                val voteAverage = movieObject.getDouble("vote_average")
                val title = movieObject.getString("title")
                val popularity = movieObject.getDouble("popularity")
                val posterPath = movieObject.getString("poster_path")
                val language = movieObject.getString("original_language")
                val originalTitle = movieObject.getString("original_title")
                val genreIdsJson = movieObject.getJSONArray("genre_ids")
                val genreIds = ArrayList<Int>()
                for (id in 0 until genreIdsJson.length()) {
                    genreIds.add(genreIdsJson.getInt(id))
                }
                val backdropPath = movieObject.getString("backdrop_path")
                val adult = movieObject.getBoolean("adult")
                val overview = movieObject.getString("overview")
                val releaseDate = movieObject.getString("release_date")
                movies.add(Movie(title,originalTitle,releaseDate,language,imdbId,overview,adult,genreIds,posterPath,backdropPath,video,voteAverage,popularity,voteCount))
            }
        } catch (ex: JSONException) {
            Log.v("Movie Extractor", ex.toString())
        }
        return movies
    }
}