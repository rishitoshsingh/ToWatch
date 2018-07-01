package com.alphae.rishi.towatch.Database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context


/**
 * Created by rishi on 1/5/18.
 */
@Database(entities = arrayOf(WatchList::class,WatchedList::class), version = 1)
abstract class WatchDatabase : RoomDatabase() {
    abstract fun watchDaoAccess(): WatchDaoAccess
    abstract fun watchedDaoAccess(): WatchedDaoAccess

    companion object {
        private var INSTANCE: WatchDatabase? = null

        fun getInstance(context: Context): WatchDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                        WatchDatabase::class.java, "towatch.db")
                        .build()
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }

    }
}
