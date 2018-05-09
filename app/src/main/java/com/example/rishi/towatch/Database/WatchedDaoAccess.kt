package com.example.rishi.towatch.Database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

/**
 * Created by rishi on 9/5/18.
 */
@Dao
interface WatchedDaoAccess {

    @Insert
    fun insertMovie(movie: WatchedList)

    @Query("SELECT * FROM watched_list WHERE movie_Id = :movieId")
    fun fetchMovie(movieId: Long): List<WatchedList>

    @Query("SELECT * FROM watched_list")
    fun fetchAllMovies(): List<WatchedList>

    @Delete
    fun deleteMovie(movie: WatchedList)

}