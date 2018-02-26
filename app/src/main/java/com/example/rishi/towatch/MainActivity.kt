package com.example.rishi.towatch

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val movies = QueryMovie().extractMovies()
        val adapter = CustomAdapter(this,movies)
        gridview.adapter = adapter

    }
}
