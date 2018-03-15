package com.example.rishi.towatch.TmdbApi

import com.example.rishi.towatch.POJOs.TmdbDiscover.DiscoverMovie
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by rishi on 13/3/18.
 */
interface TmdbApiClient {

    @GET("discover/movie?api_key=cc4b67c52acb514bdf4931f7cedfd12b&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1")
    fun getDiscoverMovie(
            @Query("api_key") apiKey: String,
            @Query("language") language: String,
            @Query("popularity") popularity: String?,
            @Query("include_adult") adult:Boolean,
            @Query("include_video") video:Boolean,
            @Query("page") page:Int
    ): Call<DiscoverMovie>



    @GET("search/movie?api_key=cc4b67c52acb514bdf4931f7cedfd12b&language=en-US&query=Red&page=1&include_adult=false")
    fun search(@Query("query") query:String):Call<DiscoverMovie>


}