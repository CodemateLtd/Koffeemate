package com.codemate.brewflop.di

import android.content.Context
import com.codemate.brewflop.BrewFlopApp
import com.codemate.brewflop.data.BrewingProgressUpdater
import dagger.Module
import dagger.Provides
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule(val app: BrewFlopApp) {
    @Provides
    @Singleton
    fun provideContext(): Context = app

    @Provides
    @Singleton
    fun provideApp() = app

    @Provides
    @Singleton
    fun provideBrewingProgressUpdater() = BrewingProgressUpdater(TimeUnit.MINUTES.toMillis(7), 30)
}