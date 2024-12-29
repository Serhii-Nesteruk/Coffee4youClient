package com.cofee4you

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger

class CoffeeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FacebookSdk.setClientToken("2261824680840025") // TODO: ONLY FOR DEV
        FacebookSdk.sdkInitialize(this)
        AppEventsLogger.activateApp(this)
    }
}