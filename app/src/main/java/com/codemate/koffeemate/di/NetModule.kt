package com.codemate.koffeemate.di

import com.codemate.koffeemate.data.network.SlackService
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