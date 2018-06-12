package com.example.rishi.towatch.Api.Clients

import com.example.rishi.towatch.POJOs.YouTube.YouTubeVideo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by rishi on 14/3/18.
 */
interface YouTubeClient {

    @GET("videos?part=snippet%2CcontentDetails%2Cstatistics")
    fun getVideoTitle(@Query("id") videoId:String,
                      @Query("key") apiKey:String):Call<YouTubeVideo>

}