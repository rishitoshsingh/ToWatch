
package com.example.rishi.towatch.POJOs.Configrations.Genres;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GenreResult {

    @SerializedName("genres")
    private List<Genre> mGenres;

    public List<Genre> getGenres() {
        return mGenres;
    }

    public void setGenres(List<Genre> genres) {
        mGenres = genres;
    }

}
