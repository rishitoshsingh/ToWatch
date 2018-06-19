
package com.example.rishi.towatch.POJOs;

import com.google.gson.annotations.SerializedName;

public class ExternalIds {

    @SerializedName("facebook_id")
    private String mFacebookId;
    @SerializedName("id")
    private Long mId;
    @SerializedName("imdb_id")
    private String mImdbId;
    @SerializedName("instagram_id")
    private String mInstagramId;
    @SerializedName("twitter_id")
    private String mTwitterId;

    public String getFacebookId() {
        return mFacebookId;
    }

    public void setFacebookId(String facebookId) {
        mFacebookId = facebookId;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getImdbId() {
        return mImdbId;
    }

    public void setImdbId(String imdbId) {
        mImdbId = imdbId;
    }

    public String getInstagramId() {
        return mInstagramId;
    }

    public void setInstagramId(String instagramId) {
        mInstagramId = instagramId;
    }

    public String getTwitterId() {
        return mTwitterId;
    }

    public void setTwitterId(String twitterId) {
        mTwitterId = twitterId;
    }

}
