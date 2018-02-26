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

    fun getTitle(): String? = this.mTitle

    fun getReleaseDate(): String? = this.mReleaseDate

    fun getLanguage(): String? = this.mLanguage

    fun getImdbId(): Int? = this.mImdbId

    fun getAdult(): Boolean = this.mAdult

    fun getGenreIds(): ArrayList<Int>? = this.mGenreIds

    fun getPosterPath(): String? = this.mPosterPath

    fun getBackdropPath(): String? = this.mBackdropPath

}