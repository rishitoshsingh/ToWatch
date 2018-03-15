
package com.example.rishi.towatch.POJOs.YouTube;

import com.google.gson.annotations.SerializedName;

public class Thumbnails {

    @SerializedName("default")
    private Default mDefault;
    @SerializedName("high")
    private High mHigh;
    @SerializedName("maxres")
    private Maxres mMaxres;
    @SerializedName("medium")
    private Medium mMedium;
    @SerializedName("standard")
    private Standard mStandard;

    public Default getDefault() {
        return mDefault;
    }

    public void setDefault(Default defaul) {
        mDefault = defaul;
    }

    public High getHigh() {
        return mHigh;
    }

    public void setHigh(High high) {
        mHigh = high;
    }

    public Maxres getMaxres() {
        return mMaxres;
    }

    public void setMaxres(Maxres maxres) {
        mMaxres = maxres;
    }

    public Medium getMedium() {
        return mMedium;
    }

    public void setMedium(Medium medium) {
        mMedium = medium;
    }

    public Standard getStandard() {
        return mStandard;
    }

    public void setStandard(Standard standard) {
        mStandard = standard;
    }

}
