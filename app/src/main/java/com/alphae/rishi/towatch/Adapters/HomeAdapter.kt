package com.alphae.rishi.towatch.Adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.alphae.rishi.towatch.Fragments.NowPlayingFragment
import com.alphae.rishi.towatch.Fragments.PopularFragment
import com.alphae.rishi.towatch.Fragments.TopRatedFragment
import com.alphae.rishi.towatch.Fragments.UpcomingFragment
import com.alphae.rishi.towatch.R

/**
 * Created by rishi on 16/3/18.
 */
class HomeAdapter(private var mContext: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> NowPlayingFragment()
            1 -> PopularFragment()
            2 -> TopRatedFragment()
            3 -> UpcomingFragment()
            else -> NowPlayingFragment()
        }
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> mContext.getString(R.string.now_playing)
            1 -> mContext.getString(R.string.popular)
            2 -> mContext.getString(R.string.top_rated)
            3 -> mContext.getString(R.string.upcoming)
            else -> mContext.getString(R.string.now_playing)
        }

    }
}