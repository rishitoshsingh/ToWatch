package com.example.rishi.towatch.Database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

/**
 * Created by rishi on 1/5/18.
 */
@Dao
interface DaoAccess {

    @Insert
    fun insertMovie(movie:WatchList)

    @Query ("SELECT * FROM WatchList WHERE id = :id")
    fun fetchMovie(id:Int)


}