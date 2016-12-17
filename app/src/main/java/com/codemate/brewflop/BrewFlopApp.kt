package com.codemate.brewflop

import android.app.Application
import io.realm.Realm

class BrewFlopApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
    }
}