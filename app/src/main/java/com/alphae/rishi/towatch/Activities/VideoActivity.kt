package com.alphae.rishi.towatch.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.alphae.rishi.towatch.Api.Clients.YouTubeClient
import com.alphae.rishi.towatch.Api.ServiceGenerator
import com.alphae.rishi.towatch.BuildConfig
import com.alphae.rishi.towatch.POJOs.YouTube.YouTubeVideo
import com.alphae.rishi.towatch.R
import com.facebook.ads.AdSize
import com.facebook.ads.AdView
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener
import kotlinx.android.synthetic.main.activity_video.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoActivity : AppCompatActivity() {

    private lateinit var mAdView: AdView
    private lateinit var mVideoId: String
    private lateinit var mMovieId: String
    private lateinit var YouTubePlayerView: YouTubePlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        window.navigationBarColor = resources.getColor(R.color.colorPrimaryDark)
        loadAdView()

        val extras = intent.extras
        if (extras != null) {
            mVideoId = extras.getString(Intent.EXTRA_TEXT)
            mMovieId = extras.getString("movieId")
            getVideoDetails()
            buttonListner()
            youtube_player_view_new.initialize({
                it.addListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady() {
                        it.loadVideo(mVideoId, 0f)
                    }
                })
            }, true)

        }

    }

    private fun buttonListner() {
        more_info.setOnClickListener {
            val intent: Intent = Intent(baseContext, MainActivity::class.java)
                    .setType("text/plain")
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .putExtra(Intent.EXTRA_TEXT, mMovieId)
            startActivity(intent)
        }
    }

    private fun getVideoDetails() {
        val ytClient = ServiceGenerator.createYtService(YouTubeClient::class.java)
        val call = ytClient.getVideoTitle(mVideoId, BuildConfig.YoutubeApiKey)
        call.enqueue(object : Callback<YouTubeVideo> {
            override fun onFailure(call: Call<YouTubeVideo>?, t: Throwable?) {}
            override fun onResponse(call: Call<YouTubeVideo>?, response: Response<YouTubeVideo>?) {
                val videoTitle: String = response?.body()?.items!![0].snippet.title
                val videoDescription: String = response.body()?.items!![0].snippet.description
                video_description.text = videoDescription
                val titleView = findViewById<TextView>(R.id.v_t)
                titleView.text = videoTitle
                Toast.makeText(this@VideoActivity,videoTitle,Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun loadAdView() {
        mAdView = AdView(this, "236732677123588_313240356139486", AdSize.BANNER_HEIGHT_50)
        fan_banner_container.addView(mAdView)
        mAdView.loadAd()
    }
}
