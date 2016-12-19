package com.codemate.koffeemate.di

import android.content.Context
import com.codemate.koffeemate.data.local.CoffeeEventRepository
import com.codemate.koffeemate.data.local.CoffeePreferences
import com.codemate.koffeemate.data.local.RealmCoffeeEventRepository
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