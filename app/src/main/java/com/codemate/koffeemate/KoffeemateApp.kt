package com.codemate.koffeemate

import android.app.Application
import com.codemate.koffeemate.data.network.SlackApi
import com.codemate.koffeemate.di.components.AppComponent
import com.codemate.koffeemate.di.components.DaggerAppComponent
import com.codemate.koffeemate.di.modules.AppModule
import com.codemate.koffeemate.di.modules.NetModule
import io.realm.Realm

class KoffeemateApp : Application() {
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