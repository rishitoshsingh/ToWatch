package com.example.rishi.towatch.Database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase



/**
 * Created by rishi on 1/5/18.
 */
@Database (entities = arrayOf(WatchList::class), version = 1 )
abstract class WatchDatabase: RoomDatabase() {
    abstract fun daoAccess():DaoAccess
}
