package com.alphae.rishi.towatch.Adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.alphae.rishi.towatch.Fragments.NowPlayingFragment
import com.alphae.rishi.towatch.Fragments.WatchListFragment
import com.alphae.rishi.towatch.Fragments.WatchedListFragment
import com.alphae.rishi.towatch.R

/**
 * Created by rishi on 9/5/18.
 */
class AccountAdapter(private var mContext: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> WatchListFragment()
            1 -> WatchedListFragment()
            else -> NowPlayingFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> mContext.getString(R.string.watch_list)
            1 -> mContext.getString(R.string.watched_list)
            else -> mContext.getString(R.string.watch_list)
        }

    }
}