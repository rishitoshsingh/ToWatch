package com.example.rishi.towatch.Database

import android.provider.BaseColumns

/**
 * Created by rishi on 8/4/18.
 */
object WatchTemp {

    object WatchListEntry : BaseColumns {
        const val TABLE_NAME = "watch_list"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_YEAR = "year"
        const val COLUMN_NAME_POSTER = "poster"
    }

}