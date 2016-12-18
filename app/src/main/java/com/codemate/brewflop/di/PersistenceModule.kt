package com.codemate.brewflop.di

import android.content.Context
import com.codemate.brewflop.data.local.CoffeeEventRepository
import com.codemate.brewflop.data.local.CoffeePreferences
import com.codemate.brewflop.data.local.RealmCoffeeEventRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PersistenceModule {
    @Provides
    @Singleton
    fun provideCoffeePreferences(ctx: Context) = CoffeePreferences(ctx)

    @Provides
    @Singleton
    fun provideCoffeeEventRepository(): CoffeeEventRepository = RealmCoffeeEventRepository()
}