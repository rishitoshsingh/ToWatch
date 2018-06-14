
package com.example.rishi.towatch.POJOs.Configrations;

import com.google.gson.annotations.SerializedName;

public class Country {

    @SerializedName("english_name")
    private String mEnglishName;
    @SerializedName("iso_3166_1")
    private String mIso31661;

    public String getEnglishName() {
        return mEnglishName;
    }

    public void setEnglishName(String englishName) {
        mEnglishName = englishName;
    }

    public String getIso31661() {
        return mIso31661;
    }

    public void setIso31661(String iso31661) {
        mIso31661 = iso31661;
    }

}
