package com.example.rishi.towatch.TmdbApi

import com.example.rishi.towatch.POJOs.Configrations.Country
import com.example.rishi.towatch.POJOs.Configrations.Genres.GenreResult
import com.example.rishi.towatch.POJOs.Configrations.Language
import com.example.rishi.towatch.POJOs.Tmdb.JsonA
import com.example.rishi.towatch.POJOs.Tmdb.JsonB
import com.example.rishi.towatch.POJOs.TmdbCollection.Collection
import com.example.rishi.towatch.POJOs.TmdbMovie.Details
import com.example.rishi.towatch.POJOs.TmdbMovie.MovieImage
import com.example.rishi.towatch.POJOs.TmdbMovie.VideoResults
import com.example.rishi.towatch.POJOs.TmdbRecommendations.Recommendations
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
            @Query("with_genres") genreId: String?,
            @Query("year") year: Int?,
            @Query("vote_average.gte") vote: Int?,
            @Query("with_original_language") language: String?,
            @Query("sort_by") sortBy: String?,
            @Query("include_adult") adult: Boolean,
            @Query("page") page: Int): Call<JsonA>


    @GET("search/movie")
    fun search(@Query("api_key") apikey: String,
               @Query("language") language: String?,
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

    @GET("configuration/languages")
    fun getLanguages(@Query("api_key") apiKey: String): Call<List<Language>>

    @GET("configuration/countries")
    fun getCountries(@Query("api_key") apiKey: String): Call<List<Country>>

    @GET("genre/movie/list")
    fun getGenres(@Query("api_key") apiKey: String): Call<GenreResult>

    @GET("collection/{collection_id}")
    fun getCollection(@Path("collection_id") collectionId: Int,
                      @Query("api_key") apiKey:String): Call<Collection>

    @GET("movie/{movie_id}/recommendations")
    fun getRecommendations(@Path("movie_id") collectionId: Int,
                           @Query("api_key") apiKey:String,
                           @Query("page") page: Int): Call<Recommendations>

    @GET("movie/{movie_id}/similar")
    fun getSimilars(@Path("movie_id") collectionId: Int,
                    @Query("api_key") apiKey:String,
                    @Query("page") page: Int): Call<JsonA>

}