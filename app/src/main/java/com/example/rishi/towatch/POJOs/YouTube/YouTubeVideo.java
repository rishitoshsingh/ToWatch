
package com.example.rishi.towatch.POJOs.YouTube;

import java.util.List;
import com.google.gson.annotations.SerializedName;
public class YouTubeVideo {

    @SerializedName("kind")
    private String mKind;
    @SerializedName("etag")
    private String mEtag;
    @SerializedName("pageInfo")
    private PageInfo mPageInfo;
    @SerializedName("items")
    private List<Item> mItems;

    public String getEtag() {
        return mEtag;
    }

    public void setEtag(String etag) {
        mEtag = etag;
    }

    public List<Item> getItems() {
        return mItems;
    }

    public void setItems(List<Item> items) {
        mItems = items;
    }

    public String getKind() {
        return mKind;
    }

    public void setKind(String kind) {
        mKind = kind;
    }

    public PageInfo getPageInfo() {
        return mPageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        mPageInfo = pageInfo;
    }

}
