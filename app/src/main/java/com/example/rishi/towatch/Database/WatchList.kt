package com.example.rishi.towatch.Database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by rishi on 1/5/18.
 */
@Entity (tableName = "watch_list")
data class WatchList (@PrimaryKey(autoGenerate = true) var id: Int?,
                      @ColumnInfo(name = "movie_name") var movieName:String,
                      @ColumnInfo(name = "movie_Id") var movieId:Long){

    constructor():this(null,"",0)
}
