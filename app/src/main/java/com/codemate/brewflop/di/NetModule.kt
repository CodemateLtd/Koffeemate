package com.codemate.brewflop.di

import com.codemate.brewflop.data.network.SlackService
import dagger.Module
import dagger.Provides
import okhttp3.HttpUrl
import javax.inject.Singleton

@Module
class NetModule(val baseUrl: HttpUrl) {
    @Provides
    @Singleton
    fun provideApi() = SlackService.getApi(baseUrl)
}