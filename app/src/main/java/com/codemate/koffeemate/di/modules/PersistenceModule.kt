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
import com.codemate.koffeemate.data.local.*
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

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository = RealmUserRepository()
}