package com.alphae.rishi.towatch.Activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.alphae.rishi.towatch.R
import com.facebook.ads.AdSize
import com.facebook.ads.AdView
import kotlinx.android.synthetic.main.content_web.*


class WebActivity : AppCompatActivity() {

    private lateinit var mAdView: AdView
    private lateinit var mWebView: WebView
    private lateinit var mPageUrl: String
    private var mPageLoading = true
    private lateinit var mCurrentUrl:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_web)

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        window.navigationBarColor = resources.getColor(R.color.colorPrimaryDark)
        loadAdView()
        mWebView = findViewById(R.id.web_view)
        val webSettings = mWebView.settings
        webSettings.javaScriptEnabled = true
        mWebView.webViewClient = MyWebViewClient()

        val extras = intent.extras
        if (extras != null) {
            mPageUrl = extras.getString(Intent.EXTRA_TEXT)
            mCurrentUrl = mPageUrl
            mWebView.loadUrl(mPageUrl)
            web_refresh.setOnRefreshListener {
                mWebView.loadUrl(mCurrentUrl)
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && this.mWebView.canGoBack()) {
            this.mWebView.goBack()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    private fun loadAdView() {
        mAdView = AdView(this, "236732677123588_313239929472862", AdSize.BANNER_HEIGHT_50)
        web_fan_banner.addView(mAdView)
        mAdView.loadAd()
    }

    private inner class MyWebViewClient : WebViewClient() {

         override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
             mCurrentUrl = url
            mPageLoading = true
        }
//
        override fun onPageCommitVisible(view: WebView, url: String) {
            super.onPageCommitVisible(view, url)
            Log.d("Web", "onPageCommitVisible Called")
            if (web_refresh != null) web_refresh.isRefreshing = false
        }

        override fun onLoadResource(view: WebView?, url: String?) {
            super.onLoadResource(view, url)
            Log.d("Web", "onLoadResource Called")
            if (web_refresh != null && mPageLoading) web_refresh.isRefreshing = true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Log.d("Web", "onPageFinished Called")
            mPageLoading = false
            if (web_refresh != null) web_refresh.isRefreshing = false
        }

        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            return false
        }
    }

}
