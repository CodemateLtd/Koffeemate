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

package com.codemate.brewflop.data.network

import okhttp3.Dispatcher
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SlackService {
    fun getApi(baseUrl: HttpUrl): SlackApi {
        return getApi(Dispatcher(), baseUrl)
    }

    fun getApi(dispatcher: Dispatcher, baseUrl: HttpUrl): SlackApi {
        val client = OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit.create(SlackApi::class.java)
    }
}
