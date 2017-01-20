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

package com.codemate.koffeemate.di.components

import com.codemate.koffeemate.di.modules.ActivityModule
import com.codemate.koffeemate.di.modules.AppModule
import com.codemate.koffeemate.di.modules.NetModule
import com.codemate.koffeemate.di.modules.PersistenceModule
import com.codemate.koffeemate.ui.userselector.UserSelectorActivity
import com.codemate.koffeemate.ui.userselector.UserSelectorFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        AppModule::class,
        PersistenceModule::class,
        NetModule::class)
)
interface AppComponent {
    fun inject(userSelectorFragment: UserSelectorFragment)

    fun plus(activityModule: ActivityModule): ActivityComponent
}