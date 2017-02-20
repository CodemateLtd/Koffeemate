/*
 * Copyright 2016 Codemate Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codemate.koffeemate.di.modules

import android.content.Context
import com.codemate.koffeemate.KoffeemateApp
import com.codemate.koffeemate.common.AndroidAwardBadgeCreator
import com.codemate.koffeemate.common.AwardBadgeCreator
import com.codemate.koffeemate.common.BrewingProgressUpdater
import dagger.Module
import dagger.Provides
import rx.android.schedulers.AndroidSchedulers
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
    fun provideBrewingProgressUpdater() = BrewingProgressUpdater(observerScheduler = AndroidSchedulers.mainThread())

    @Provides
    @Singleton
    fun provideAwardBadgeCreator(ctx: Context): AwardBadgeCreator = AndroidAwardBadgeCreator(ctx)
}