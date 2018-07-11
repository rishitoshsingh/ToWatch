
package com.alphae.rishi.towatch.POJOs.TmdbFind;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Find {

    @SerializedName("movie_results")
    private List<MovieResult> mMovieResults;
    @SerializedName("person_results")
    private List<Object> mPersonResults;
    @SerializedName("tv_episode_results")
    private List<Object> mTvEpisodeResults;
    @SerializedName("tv_results")
    private List<Object> mTvResults;
    @SerializedName("tv_season_results")
    private List<Object> mTvSeasonResults;

    public List<MovieResult> getMovieResults() {
        return mMovieResults;
    }

    public void setMovieResults(List<MovieResult> movieResults) {
        mMovieResults = movieResults;
    }

    public List<Object> getPersonResults() {
        return mPersonResults;
    }

    public void setPersonResults(List<Object> personResults) {
        mPersonResults = personResults;
    }

    public List<Object> getTvEpisodeResults() {
        return mTvEpisodeResults;
    }

    public void setTvEpisodeResults(List<Object> tvEpisodeResults) {
        mTvEpisodeResults = tvEpisodeResults;
    }

    public List<Object> getTvResults() {
        return mTvResults;
    }

    public void setTvResults(List<Object> tvResults) {
        mTvResults = tvResults;
    }

    public List<Object> getTvSeasonResults() {
        return mTvSeasonResults;
    }

    public void setTvSeasonResults(List<Object> tvSeasonResults) {
        mTvSeasonResults = tvSeasonResults;
    }

}
