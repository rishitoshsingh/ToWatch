package com.example.rishi.towatch.TmdbApi

import com.example.rishi.towatch.POJOs.Tmdb.JsonA
import com.example.rishi.towatch.POJOs.Tmdb.JsonB
import com.example.rishi.towatch.POJOs.TmdbMovie.Details
import com.example.rishi.towatch.POJOs.TmdbMovie.MovieImage
import com.example.rishi.towatch.POJOs.TmdbMovie.VideoResults
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by rishi on 13/3/18.
 */
interface TmdbApiClient {

    @GET("discover/movie")
    fun getDiscoverMovie(
            @Query("api_key") apiKey: String,
            @Query("language") language: String,
            @Query("popularity") popularity: String?,
            @Query("include_adult") adult: Boolean,
            @Query("include_video") video: Boolean,
            @Query("page") page: Int): Call<JsonA>


    @GET("search/movie")
    fun search(@Query("api_key") apikey: String,
               @Query("language") language: String,
               @Query("include_adult") adult: Boolean,
               @Query("page") page: Int,
               @Query("query") query: String): Call<JsonA>

    @GET("movie/now_playing")
    fun getNowPlaying(@Query("api_key") apikey: String,
                      @Query("language") language: String,
                      @Query("page") page: Int,
                      @Query("region") region: String?): Call<JsonB>

    @GET("movie/popular")
    fun getPopular(@Query("api_key") apikey: String,
                   @Query("language") language: String,
                   @Query("page") page: Int,
                   @Query("region") region: String?): Call<JsonA>

    @GET("movie/top_rated")
    fun getToprated(@Query("api_key") apikey: String,
                    @Query("language") language: String,
                    @Query("page") page: Int,
                    @Query("region") region: String?): Call<JsonA>

    @GET("movie/upcoming")
    fun getUpcoming(@Query("api_key") apikey: String,
                    @Query("language") language: String,
                    @Query("page") page: Int,
                    @Query("region") region: String?): Call<JsonB>

    @GET("movie/{movie_id}")
    fun getMovieDetails(@Path("movie_id") movieId: Int,
                        @Query("api_key") apiKey: String,
                        @Query("language") language: String): Call<Details>

    @GET("movie/{movie_id}/images")
    fun getMovieImages(@Path("movie_id") movieId: Int,
                       @Query("api_key") apiKey: String): Call<MovieImage>

    @GET("movie/{movie_id}/videos")
    fun getMovieVideos(@Path("movie_id") movieId: Int,
                       @Query("api_key") apiKey: String,
                       @Query("language") language: String): Call<VideoResults>

}