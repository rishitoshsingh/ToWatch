
package com.alphae.rishi.towatch.POJOs.YouTube;

import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("contentDetails")
    private ContentDetails mContentDetails;
    @SerializedName("etag")
    private String mEtag;
    @SerializedName("id")
    private String mId;
    @SerializedName("kind")
    private String mKind;
    @SerializedName("snippet")
    private Snippet mSnippet;
    @SerializedName("statistics")
    private Statistics mStatistics;

    public ContentDetails getContentDetails() {
        return mContentDetails;
    }

    public void setContentDetails(ContentDetails contentDetails) {
        mContentDetails = contentDetails;
    }

    public String getEtag() {
        return mEtag;
    }

    public void setEtag(String etag) {
        mEtag = etag;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getKind() {
        return mKind;
    }

    public void setKind(String kind) {
        mKind = kind;
    }

    public Snippet getSnippet() {
        return mSnippet;
    }

    public void setSnippet(Snippet snippet) {
        mSnippet = snippet;
    }

    public Statistics getStatistics() {
        return mStatistics;
    }

    public void setStatistics(Statistics statistics) {
        mStatistics = statistics;
    }

}
