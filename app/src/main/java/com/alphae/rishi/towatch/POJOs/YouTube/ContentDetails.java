
package com.alphae.rishi.towatch.POJOs.YouTube;

import com.google.gson.annotations.SerializedName;

public class ContentDetails {

    @SerializedName("caption")
    private String mCaption;
    @SerializedName("definition")
    private String mDefinition;
    @SerializedName("dimension")
    private String mDimension;
    @SerializedName("duration")
    private String mDuration;
    @SerializedName("licensedContent")
    private Boolean mLicensedContent;
    @SerializedName("projection")
    private String mProjection;

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getDefinition() {
        return mDefinition;
    }

    public void setDefinition(String definition) {
        mDefinition = definition;
    }

    public String getDimension() {
        return mDimension;
    }

    public void setDimension(String dimension) {
        mDimension = dimension;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        mDuration = duration;
    }

    public Boolean getLicensedContent() {
        return mLicensedContent;
    }

    public void setLicensedContent(Boolean licensedContent) {
        mLicensedContent = licensedContent;
    }

    public String getProjection() {
        return mProjection;
    }

    public void setProjection(String projection) {
        mProjection = projection;
    }

}
