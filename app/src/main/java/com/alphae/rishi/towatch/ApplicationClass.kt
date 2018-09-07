package com.alphae.rishi.towatch

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.multidex.MultiDex
import android.util.Log
import com.alphae.rishi.towatch.Activities.MainActivity
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
            val movieIdKey: String?

            if (data != null) {
                movieIdKey = data.optString("movieIdKey", null)
                if (movieIdKey != null)
                    Log.i("OneSignalExample", "customkey set with value: $movieIdKey")

                val sharedPreferences: SharedPreferences = getSharedPreferences("Notification", Context.MODE_PRIVATE)
                val sharedPreferenceEditor: SharedPreferences.Editor = sharedPreferences.edit()
                sharedPreferenceEditor.putBoolean("Clicked", true)
                sharedPreferenceEditor.commit()

                val intent: Intent = Intent(baseContext, MainActivity::class.java)
                        .setType("text/plain")
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .putExtra(Intent.EXTRA_TEXT, movieIdKey)
                startActivity(intent)

            }

        }
    }

}
