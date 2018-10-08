package com.alphae.rishi.towatch

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.multidex.MultiDex
import android.util.Log
import com.alphae.rishi.towatch.Activities.MainActivity
import com.alphae.rishi.towatch.Activities.VideoActivity
import com.onesignal.OSNotification
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal


/**
 * Created by rishi on 29/7/18.
 */

class ApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()

        // OneSignal Initialization
        OneSignal.startInit(this)
                .setNotificationReceivedHandler(ExampleNotificationReceivedHandler())
                .setNotificationOpenedHandler(ExampleNotificationOpenedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .init()

    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    private inner class ExampleNotificationReceivedHandler : OneSignal.NotificationReceivedHandler {
        override fun notificationReceived(notification: OSNotification) {
            val data = notification.payload.additionalData
            val notificationID = notification.payload.notificationID
            val title = notification.payload.title
            val body = notification.payload.body
            val smallIcon = notification.payload.smallIcon
            val largeIcon = notification.payload.largeIcon
            val bigPicture = notification.payload.bigPicture
            val smallIconAccentColor = notification.payload.smallIconAccentColor
            val sound = notification.payload.sound
            val ledColor = notification.payload.ledColor
            val lockScreenVisibility = notification.payload.lockScreenVisibility
            val groupKey = notification.payload.groupKey
            val groupMessage = notification.payload.groupMessage
            val fromProjectNumber = notification.payload.fromProjectNumber
            val rawPayload = notification.payload.rawPayload

            val customKey: String?

            Log.i("OneSignalExample", "NotificationID received: $notificationID")

            if (data != null) {
                customKey = data.optString("movieIdKey", null)
                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: $customKey")
            }
        }
    }


    private inner class ExampleNotificationOpenedHandler : OneSignal.NotificationOpenedHandler {
        override fun notificationOpened(result: OSNotificationOpenResult?) {

            val actionType = result?.action?.type
            val data = result?.notification?.payload?.additionalData

            if (data != null) {
                val notificationType = data.optInt("type", 0)
                Log.d("Notification",notificationType.toString())
                when (notificationType) {
                    1 -> {
                        val movieId = data.optString("movieId", null)
                        openMovie(movieId)
                    }
                    2 -> {
                        val movieId = data.optString("movieId", null)
                        val videoId = data.optString("videoId", null)
                        playVideo(videoId,movieId)
                    }
                    3 -> {
                        val pageUrl = data.optString("webpageUrl", "https://play.google.com/store/apps/details?id=com.alphae.rishi.towatch")
                        openWebView(pageUrl)
                    }
                    else -> {
                        val pageUrl = data.optString("webpageUrl", "https://play.google.com/store/apps/details?id=com.alphae.rishi.towatch")
                        openWebView(pageUrl)
                    }
                }
            }
        }
    }

    private fun openMovie(movieId: String?) {

        if (movieId != null) {
            Log.i("OneSignalExample", "customkey set with value: $movieId")
            val sharedPreferences: SharedPreferences = getSharedPreferences("Notification", Context.MODE_PRIVATE)
            val sharedPreferenceEditor: SharedPreferences.Editor = sharedPreferences.edit()
            sharedPreferenceEditor.putBoolean("Clicked", true)
            sharedPreferenceEditor.commit()

            val intent: Intent = Intent(baseContext, MainActivity::class.java)
                    .setType("text/plain")
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .putExtra(Intent.EXTRA_TEXT, movieId)
            startActivity(intent)
        }
    }

    private fun playVideo(videoId: String?,movieId: String?) {
        if (videoId != null) {
            Log.i("OneSignalExample", "customkey set with value: $videoId")
            val sharedPreferences: SharedPreferences = getSharedPreferences("Notification", Context.MODE_PRIVATE)
            val sharedPreferenceEditor: SharedPreferences.Editor = sharedPreferences.edit()
            sharedPreferenceEditor.putBoolean("Clicked", true)
            sharedPreferenceEditor.commit()

            val intent: Intent = Intent(baseContext, VideoActivity::class.java)
                    .setType("text/plain")
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .putExtra(Intent.EXTRA_TEXT, videoId)
                    .putExtra("movieId",movieId)
            startActivity(intent)
        }
    }

    private fun openWebView(pageUrl: String?) {}

}
