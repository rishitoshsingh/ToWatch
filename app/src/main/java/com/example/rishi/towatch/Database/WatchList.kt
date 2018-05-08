package com.example.rishi.towatch.Database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by rishi on 1/5/18.
 */
@Entity (tableName = "watch_list")
data class WatchList (@ColumnInfo(name = "movie_Name") var movieName:String,
                      @PrimaryKey @ColumnInfo(name = "movie_Id") var movieId:Long,
                      @ColumnInfo(name = "movie_Poster") var moviePoster:String,
                      @ColumnInfo(name = "movie_Release_Date") var movieReleaseDate:String){

    constructor():this("",0,"","")
}

//@Entity (tableName = "watch_list")
//data class WatchList (@PrimaryKey(autoGenerate = true) var id: Int?,
//                      @ColumnInfo(name = "movie_Name") var movieName:String,
//                      @ColumnInfo(name = "movie_Id") var movieId:Long,
//                      @ColumnInfo(name = "movie_Poster") var moviePoster:String,
//                      @ColumnInfo(name = "movie_Release_Date") var movieReleaseDate:String){
//
//    constructor():this(null,"",0,"","")
//}
