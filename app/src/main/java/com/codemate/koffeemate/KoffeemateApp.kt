package com.codemate.koffeemate

import android.app.Application
import com.codemate.koffeemate.data.local.Migration
import com.codemate.koffeemate.data.network.SlackApi
import com.codemate.koffeemate.di.components.AppComponent
import com.codemate.koffeemate.di.components.DaggerAppComponent
import com.codemate.koffeemate.di.modules.AppModule
import com.codemate.koffeemate.di.modules.NetModule
import io.realm.Realm
import io.realm.RealmConfiguration

class KoffeemateApp : Application() {
    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        initializeRealm()

        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .netModule(NetModule(SlackApi.BASE_URL))
                .build()
    }

    private fun initializeRealm() {
        Realm.init(this)

        val configuration = RealmConfiguration.Builder()
                .migration(Migration())
                .schemaVersion(1)
                .build()
        Realm.setDefaultConfiguration(configuration)
    }
}