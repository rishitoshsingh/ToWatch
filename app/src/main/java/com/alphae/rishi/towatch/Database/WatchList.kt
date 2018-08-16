package com.alphae.rishi.towatch.Database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

/**
 * Created by rishi on 1/5/18.
 */

@Entity (tableName = "watch_list")
data class WatchList (@ColumnInfo(name = "movie_Name") var movieName:String,
                      @PrimaryKey @ColumnInfo(name = "movie_Id") var movieId:Long,
                      @ColumnInfo(name = "movie_Poster") var moviePoster:String,
                      @ColumnInfo(name = "movie_Release_Date") var movieReleaseDate:String) : Serializable {

    constructor():this("",0,"","")

}