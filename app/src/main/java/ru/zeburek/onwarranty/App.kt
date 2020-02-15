package ru.zeburek.onwarranty

import android.app.Application
import android.content.res.Resources


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        appResources = resources
    }

    companion object {
        var appResources: Resources? = null
            private set
    }
}