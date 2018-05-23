package com.example.rishi.towatch.Adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rishi.towatch.R

/**
 * Created by rishi on 21/5/18.
 */
class CompaniesAdapter(context:Context,urls:ArrayList<String>):ArrayAdapter<String>(context,0,urls) {

    private val LOGO_BASE_URL = "https://image.tmdb.org/t/p/w154/"

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var listItemView: View? = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.production_company_image_layout, parent, false)
        }
        val imageView = listItemView?.findViewById<ImageView>(R.id.company_logo) as ImageView

        val url = getItem(position)

        val logoUri: Uri = Uri.parse(LOGO_BASE_URL + url)

        Glide.with(context)
                .load(logoUri)
                .apply(RequestOptions()
                        .fitCenter())
                .into(imageView)


        return listItemView
    }
}