package com.example.rishi.towatch

/**
 * Created by rishi on 25/2/18.
 */
class Movie {
    var mTitle: String? = null
    var mYear: String? = null
    var mLanguage: String? = null
    var mImdbId: Int? = null
    var mOverview: String? = null
    var mAdult: Boolean = false
    var mGenreIds: Array<Int>? = null
    var mPosterPath: String? = null
    var mBackdropPath: String? = null

    constructor(
            title: String,
            year: String,
            language: String,
            imdbId: Int,
            overview: String,
            adult: Boolean,
            genreIds: Array<Int>,
            posterPath: String,
            backdropPath: String
    ) {
        this.mTitle = title
        this.mYear = year
        this.mLanguage = language
        this.mPosterPath = posterPath
        this.mImdbId = imdbId
        this.mOverview = overview
        this.mAdult = adult
        this.mGenreIds = genreIds
        this.mBackdropPath = backdropPath
    }

    fun getTitle(): String? {
        return this.mTitle
    }

    fun getYear(): String? {
        return this.mYear
    }

    fun getLanguage(): String? {
        return this.mLanguage
    }

    fun getPosterPath(): String? {
        return this.mPosterPath
    }

    fun getImdbId(): Int? {
        return this.mImdbId
    }

    fun getAdult(): Boolean {
        return this.mAdult
    }

    fun getGenreIds(): Array<Int>? {
        return this.mGenreIds
    }

    fun getBackdropPath(): String? {
        return this.mBackdropPath
    }

}