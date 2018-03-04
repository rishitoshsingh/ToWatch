package com.example.rishi.towatch

import java.io.Serializable

/**
 * Created by rishi on 25/2/18.
 */
class Movie : Serializable{

    private var mTitle: String? = null
    private val mOriginalTitle: String
    private var mReleaseDate: String? = null
    private var mLanguage: String? = null
    private var mImdbId: Int? = null
    private var mOverview: String? = null
    private var mAdult: Boolean = false
    private var mGenreIds: ArrayList<Int>
    private var mPosterPath: String? = null
    private var mBackdropPath: String? = null
    private val mVideo: Boolean
    private val mVoteAverage: Double
    private val mPopularity: Double
    private val mVoteCount: Int

    constructor(
            mTitle: String?,
            mOriginalTitle: String,
            mReleaseDate: String?,
            mLanguage: String?,
            mImdbId: Int?,
            mOverview: String?,
            mAdult: Boolean,
            mGenreIds: ArrayList<Int>,
            mPosterPath: String?,
            mBackdropPath: String?,
            mVideo: Boolean,
            mVoteAverage: Double,
            mPopularity: Double,
            mVoteCount: Int
    ) {
        this.mTitle = mTitle
        this.mOriginalTitle = mOriginalTitle
        this.mReleaseDate = mReleaseDate
        this.mLanguage = mLanguage
        this.mImdbId = mImdbId
        this.mOverview = mOverview
        this.mAdult = mAdult
        this.mGenreIds = mGenreIds
        this.mPosterPath = mPosterPath
        this.mBackdropPath = mBackdropPath
        this.mVideo = mVideo
        this.mVoteAverage = mVoteAverage
        this.mPopularity = mPopularity
        this.mVoteCount = mVoteCount
    }

    fun getTitle(): String? = this.mTitle

    fun getOriginalTitle(): String? = this.mOriginalTitle

    fun getReleaseDate(): String? = this.mReleaseDate

    fun getLanguage(): String? = this.mLanguage

    fun getImdbId(): Int? = this.mImdbId

    fun getOverview() : String? = this.mOverview

    fun getAdult(): Boolean = this.mAdult

    fun getGenreIds(): ArrayList<Int> = this.mGenreIds

    fun getPosterPath(): String? = this.mPosterPath

    fun getBackdropPath(): String? = this.mBackdropPath

    fun getVideo(): Boolean = this.mVideo

    fun getVoteAverage():Double = this.mVoteAverage

    fun getPopularity():Double=this.mPopularity

    fun getVoteCount():Int = this.mVoteCount

    override fun toString(): String {
        return "Movie(mTitle=$mTitle, mReleaseDate=$mReleaseDate, mLanguage=$mLanguage, mImdbId=$mImdbId, mOverview=$mOverview, mAdult=$mAdult, mGenreIds=$mGenreIds, mPosterPath=$mPosterPath, mBackdropPath=$mBackdropPath)"
    }


}
