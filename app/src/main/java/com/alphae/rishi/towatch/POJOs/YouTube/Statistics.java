
package com.alphae.rishi.towatch.POJOs.YouTube;

import com.google.gson.annotations.SerializedName;

public class Statistics {

    @SerializedName("commentCount")
    private String mCommentCount;
    @SerializedName("dislikeCount")
    private String mDislikeCount;
    @SerializedName("favoriteCount")
    private String mFavoriteCount;
    @SerializedName("likeCount")
    private String mLikeCount;
    @SerializedName("viewCount")
    private String mViewCount;

    public String getCommentCount() {
        return mCommentCount;
    }

    public void setCommentCount(String commentCount) {
        mCommentCount = commentCount;
    }

    public String getDislikeCount() {
        return mDislikeCount;
    }

    public void setDislikeCount(String dislikeCount) {
        mDislikeCount = dislikeCount;
    }

    public String getFavoriteCount() {
        return mFavoriteCount;
    }

    public void setFavoriteCount(String favoriteCount) {
        mFavoriteCount = favoriteCount;
    }

    public String getLikeCount() {
        return mLikeCount;
    }

    public void setLikeCount(String likeCount) {
        mLikeCount = likeCount;
    }

    public String getViewCount() {
        return mViewCount;
    }

    public void setViewCount(String viewCount) {
        mViewCount = viewCount;
    }

}
