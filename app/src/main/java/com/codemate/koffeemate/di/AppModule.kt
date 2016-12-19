package com.codemate.koffeemate.di

import android.content.Context
import com.codemate.koffeemate.KoffeemateApp
import com.codemate.koffeemate.data.AndroidAwardBadgeCreator
import com.codemate.koffeemate.data.AwardBadgeCreator
import com.codemate.koffeemate.data.BrewingProgressUpdater
import dagger.Module
import dagger.Provides
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule(val app: KoffeemateApp) {
    @Provides
    @Singleton
    fun provideContext(): Context = app

    @Provides
    @Singleton
    fun provideApp() = app

    @Provides
    @Singleton
    fun provideBrewingProgressUpdater() = BrewingProgressUpdater(TimeUnit.MINUTES.toMillis(7), 30)

    @Provides
    fun provideAwardBadgeCreator(ctx: Context): AwardBadgeCreator = AndroidAwardBadgeCreator(ctx)
}