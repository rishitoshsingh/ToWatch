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
 * Created by rishi on 27/2/18.
 */
class QueryYoutube {

    fun getYoutubeResponse(urlString: String?): String {
        val url = createUrl(urlString)
        var jsonReponse: String = ""
        try {
            jsonReponse = makeHttpRequest(url)
        } catch (ex: IOException) {
            Log.v("getYoutubeResponse","Exception",ex)
        }

        return  extractTitle(jsonReponse)
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
                Log.v("makeHttpRequest", "Youtube Connection Failed with response code " + httpConnection.responseCode.toString())
            }
        } catch (ex: Exception) {
            Log.v("makeHttpRequest", "Exception", ex)
        }
        return  jsonResponse
    }

    private fun readFromStream(inputStream: InputStream?): String {
        val output:StringBuilder = StringBuilder()
        if(inputStream!=null){
            val inputStreamReader: InputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
            val bufferReader: BufferedReader = BufferedReader(inputStreamReader)
            var line = bufferReader.readLine()
            while(line != null){
                output.append(line)
                line = bufferReader.readLine()
            }
        }
        return  output.toString()
    }

    private fun createUrl(urlString: String?): URL? {
        var url:URL? = null
        try {
            url = URL(urlString)
        }catch (ex:MalformedURLException){
            Log.v("createUrl","Exception : ",ex)
        }
        return url
    }

    private fun extractTitle(jsonReponse: String): String {

        var title:String = ""
        try{
            val root = JSONObject(jsonReponse)
            val items = root.getJSONArray("items")
            val details = items.getJSONObject(0)
            val snippet = details.getJSONObject("snippet")
            title = snippet.getString("title")
        }catch (ex:JSONException){
            Log.v("extractTitle","Json : ",ex)
        }
        return title
    }



}