package com.example.rishi.towatch.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by rishi on 8/4/18.
 */
class WatchListDbHelper(context:Context):SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VRESION) {

    companion object {
        val DATABASE_NAME = "towatch.db"
        val DATABASE_VRESION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val SQL_CREATE_TOWATCH_TABLE = "CREATE TABLE " + WatchList.WatchListEntry.TABLE_NAME + "(" + WatchList.WatchListEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " + WatchList.WatchListEntry.COLUMN_NAME_TITLE + "TEXT NOT NULL, " + WatchList.WatchListEntry.COLUMN_NAME_YEAR + "INTEGER NOT NULL, " + WatchList.WatchListEntry.COLUMN_NAME_POSTER + "TEXT NOT NULL);"
        db?.execSQL(SQL_CREATE_TOWATCH_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}