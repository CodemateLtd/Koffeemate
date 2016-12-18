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

package com.codemate.brewflop

import android.app.Application
import com.codemate.brewflop.data.network.SlackApi
import com.codemate.brewflop.di.AppComponent
import com.codemate.brewflop.di.AppModule
import com.codemate.brewflop.di.DaggerAppComponent
import com.codemate.brewflop.di.NetModule
import io.realm.Realm

class BrewFlopApp : Application() {
    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .netModule(NetModule(SlackApi.BASE_URL))
                .build()
    }
}