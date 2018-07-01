
package com.alphae.rishi.towatch.POJOs.TmdbCollection;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Collection {

    @SerializedName("backdrop_path")
    private String mBackdropPath;
    @SerializedName("id")
    private Long mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("overview")
    private String mOverview;
    @SerializedName("parts")
    private List<Part> mParts;
    @SerializedName("poster_path")
    private String mPosterPath;

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        mBackdropPath = backdropPath;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public List<Part> getParts() {
        return mParts;
    }

    public void setParts(List<Part> parts) {
        mParts = parts;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

}
