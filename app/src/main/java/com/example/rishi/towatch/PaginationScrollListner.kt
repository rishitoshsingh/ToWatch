package com.example.rishi.towatch

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * Created by rishi on 15/3/18.
 */
abstract class PaginationScrollListner(layoutManager: GridLayoutManager): RecyclerView.OnScrollListener() {

    var mLayoutManager:GridLayoutManager = layoutManager

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val visibleItemCount = mLayoutManager.childCount
        val totalItemCount = mLayoutManager.itemCount
        val firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition()

        if (!isLoading() && !isLastPage()) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && getCurrentPage() < getTotalPageCount()) {
                loadMoreItems()
            }
        }
    }


    protected abstract fun loadMoreItems()

    abstract fun getTotalPageCount(): Int

    abstract fun isLastPage(): Boolean

    abstract fun isLoading(): Boolean

    abstract fun getCurrentPage(): Int

}