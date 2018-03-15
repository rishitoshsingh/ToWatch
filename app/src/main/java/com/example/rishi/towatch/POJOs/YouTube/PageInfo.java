
package com.example.rishi.towatch.POJOs.YouTube;

import com.google.gson.annotations.SerializedName;

public class PageInfo {

    @SerializedName("resultsPerPage")
    private Long mResultsPerPage;
    @SerializedName("totalResults")
    private Long mTotalResults;

    public Long getResultsPerPage() {
        return mResultsPerPage;
    }

    public void setResultsPerPage(Long resultsPerPage) {
        mResultsPerPage = resultsPerPage;
    }

    public Long getTotalResults() {
        return mTotalResults;
    }


    public void setTotalResults(Long totalResults) {
        mTotalResults = totalResults;
    }
    @Override
    public String toString() {
        return super.toString();
    }
}
