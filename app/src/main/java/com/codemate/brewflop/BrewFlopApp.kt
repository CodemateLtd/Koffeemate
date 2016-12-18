package com.codemate.brewflop

import android.app.Application
import com.codemate.brewflop.data.network.SlackApi
import com.codemate.brewflop.di.AppComponent
import com.codemate.brewflop.di.AppModule
import com.codemate.brewflop.di.DaggerAppComponent
import com.codemate.brewflop.di.NetModule
import io.realm.Realm

class BrewFlopApp : Application() {
    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .netModule(NetModule(SlackApi.BASE_URL))
                .build()
    }
}