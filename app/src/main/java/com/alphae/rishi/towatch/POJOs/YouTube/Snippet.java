
package com.alphae.rishi.towatch.POJOs.YouTube;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Snippet {

    @SerializedName("categoryId")
    private String mCategoryId;
    @SerializedName("channelId")
    private String mChannelId;
    @SerializedName("channelTitle")
    private String mChannelTitle;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("liveBroadcastContent")
    private String mLiveBroadcastContent;
    @SerializedName("localized")
    private Localized mLocalized;
    @SerializedName("publishedAt")
    private String mPublishedAt;
    @SerializedName("tags")
    private List<String> mTags;
    @SerializedName("thumbnails")
    private Thumbnails mThumbnails;
    @SerializedName("title")
    private String mTitle;

    public String getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(String categoryId) {
        mCategoryId = categoryId;
    }

    public String getChannelId() {
        return mChannelId;
    }

    public void setChannelId(String channelId) {
        mChannelId = channelId;
    }

    public String getChannelTitle() {
        return mChannelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        mChannelTitle = channelTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getLiveBroadcastContent() {
        return mLiveBroadcastContent;
    }

    public void setLiveBroadcastContent(String liveBroadcastContent) {
        mLiveBroadcastContent = liveBroadcastContent;
    }

    public Localized getLocalized() {
        return mLocalized;
    }

    public void setLocalized(Localized localized) {
        mLocalized = localized;
    }

    public String getPublishedAt() {
        return mPublishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        mPublishedAt = publishedAt;
    }

    public List<String> getTags() {
        return mTags;
    }

    public void setTags(List<String> tags) {
        mTags = tags;
    }

    public Thumbnails getThumbnails() {
        return mThumbnails;
    }

    public void setThumbnails(Thumbnails thumbnails) {
        mThumbnails = thumbnails;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

}
