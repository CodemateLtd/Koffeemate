package com.codemate.brewflop.injection;

import com.codemate.brewflop.data.network.SlackApi;
import com.codemate.brewflop.data.network.SlackService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by iiro on 12.10.2016.
 */
@Module
public class NetModule {
    String baseUrl;

    public NetModule(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Provides
    @Singleton
    SlackApi provideSlackApi() {
        return new SlackService(baseUrl).getApi();
    }
}
