package com.example.rishi.towatch.Adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Parcelable
import android.view.ViewGroup
import android.view.LayoutInflater
import android.support.v4.view.PagerAdapter
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.rishi.towatch.R
import java.net.URI


/**
 * Created by rishi on 21/6/18.
 */
class SlidingImageAdapter(private val mContext: Context, private val imagesUrls: ArrayList<String>) : PagerAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(mContext)
    private val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w1280/"

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return imagesUrls.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater.inflate(R.layout.backdrop_image_layout, view, false)!!

        val imageView = imageLayout.findViewById(R.id.backdropViewPagerImage) as ImageView

        val url = IMAGE_BASE_URL + imagesUrls[position]
        val uri = Uri.parse(url)

        Glide.with(mContext)
                .load(uri)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        imageLayout.findViewById<ProgressBar>(R.id.backdropViewPagerProgressBar).visibility = View.GONE
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        imageLayout.findViewById<ProgressBar>(R.id.backdropViewPagerProgressBar).visibility = View.GONE
                        return false
                    }
                })
                .apply(RequestOptions()
                        .error(R.drawable.poster_placeholder)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                .into(imageView)

        view.addView(imageLayout, 0)

        return imageLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }


}