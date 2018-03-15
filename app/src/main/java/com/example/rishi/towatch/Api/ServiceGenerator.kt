package com.example.rishi.towatch.Api

import com.example.rishi.towatch.TmdbApi.TmdbApiClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by rishi on 13/3/18.
 */
class ServiceGenerator {

    companion object {

        private val builder:Retrofit.Builder = Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
        private val retrofit:Retrofit = builder.build()

        fun <S> createService(serviceClass: Class<S>):S {
            return retrofit.create(serviceClass)
        }


    }

}