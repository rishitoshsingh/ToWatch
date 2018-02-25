package com.example.rishi.towatch

/**
 * Created by rishi on 25/2/18.
 */
class Movie {
    var mTitle: String? = null
    var mReleaseDate: String? = null
    var mLanguage: String? = null
    var mImdbId: Int? = null
    var mOverview: String? = null
    var mAdult: Boolean = false
    var mGenreIds: ArrayList<Int>? = null
    var mPosterPath: String? = null
    var mBackdropPath: String? = null

    constructor(
            title: String,
            releaseDate: String,
            language: String,
            imdbId: Int,
            overview: String,
            adult: Boolean,
            genreIds: ArrayList<Int>,
            posterPath: String,
            backdropPath: String
    ) {
        this.mTitle = title
        this.mReleaseDate = releaseDate
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

    fun getReleaseDate(): String? {
        return this.mReleaseDate
    }

    fun getLanguage(): String? {
        return this.mLanguage
    }

    fun getImdbId(): Int? {
        return this.mImdbId
    }

    fun getAdult(): Boolean {
        return this.mAdult
    }

    fun getGenreIds(): ArrayList<Int>? {
        return this.mGenreIds
    }

    fun getPosterPath(): String? {
        return this.mPosterPath
    }

    fun getBackdropPath(): String? {
        return this.mBackdropPath
    }

}