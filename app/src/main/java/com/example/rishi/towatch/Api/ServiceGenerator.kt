package com.example.rishi.towatch.Api

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

        private val ytBuilder:Retrofit.Builder = Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/youtube/v3/")
                .addConverterFactory(GsonConverterFactory.create())
        private val ytRetrofit:Retrofit = ytBuilder.build()


        fun <S> createService(serviceClass: Class<S>):S {
            return retrofit.create(serviceClass)
        }

        fun <S> createYtService(serviceClass: Class<S>):S {
            return ytRetrofit.create(serviceClass)
        }

    }

}