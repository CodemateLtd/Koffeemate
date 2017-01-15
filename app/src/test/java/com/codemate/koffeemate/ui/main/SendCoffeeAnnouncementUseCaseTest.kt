/*
 * Copyright 2017 Codemate Ltd
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

package com.codemate.koffeemate.ui.main

import android.content.SharedPreferences
import android.os.Handler
import com.codemate.koffeemate.BuildConfig
import com.codemate.koffeemate.common.BrewingProgressUpdater
import com.codemate.koffeemate.data.local.CoffeeEventRepository
import com.codemate.koffeemate.data.local.CoffeePreferences
import com.codemate.koffeemate.data.network.SlackApi
import com.codemate.koffeemate.data.network.SlackService
import com.codemate.koffeemate.testutils.SynchronousExecutorService
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import okhttp3.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class SendCoffeeAnnouncementUseCaseTest {
    val CHANNEL_NAME = "fake-channel"

    lateinit var updater: BrewingProgressUpdater

    @Mock
    lateinit var mockCoffeePreferences: CoffeePreferences

    lateinit var mockServer: MockWebServer
    lateinit var slackApi: SlackApi

    @Mock
    lateinit var mockCoffeeEventRepository: CoffeeEventRepository

    lateinit var useCase: SendCoffeeAnnouncementUseCase

    lateinit var mockUpdateListener: (Int) -> Unit
    lateinit var mockCompleteListener: () -> Unit

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        updater = BrewingProgressUpdater(9, 3)
        updater.updateHandler = mock<Handler>()

        mockCoffeePreferences.preferences = mock<SharedPreferences>()
        whenever(mockCoffeePreferences.getCoffeeAnnouncementChannel()).thenReturn(CHANNEL_NAME)

        mockServer = MockWebServer()
        mockServer.start()

        slackApi = SlackService.getApi(
                Dispatcher(SynchronousExecutorService()),
                mockServer.url("/")
        )

        useCase = SendCoffeeAnnouncementUseCase(
                updater,
                mockCoffeePreferences,
                slackApi,
                mockCoffeeEventRepository
        )

        mockUpdateListener = mock<(Int) -> Unit>()
        mockCompleteListener = mock<() -> Unit>()
    }

    @Test
    fun execute_WhenResponseSuccessful_UpdatesCoffeeProgressAndPostsToSlack() {
        mockServer.enqueue(MockResponse().setBody(""))
        useCase.execute("A happy message about coffee", mockUpdateListener, mockCompleteListener)

        updater.run()
        updater.run()
        updater.run()

        inOrder(mockUpdateListener, mockCompleteListener, mockCoffeeEventRepository) {
            verify(mockUpdateListener).invoke(10)
            verify(mockUpdateListener).invoke(33)
            verify(mockUpdateListener).invoke(67)

            verify(mockCoffeeEventRepository).recordBrewingEvent()
            verify(mockCompleteListener).invoke()
        }

        val apiRequest = mockServer.takeRequest()
        assertThat(apiRequest.path, equalTo("/chat.postMessage"))

        val requestBody = apiRequest.body.readUtf8()
        assertThat(requestBody, containsString("token=${BuildConfig.SLACK_AUTH_TOKEN}"))
        assertThat(requestBody, containsString("channel=$CHANNEL_NAME"))
        assertThat(requestBody, containsString("text=A%20happy%20message%20about%20coffee"))
        assertThat(requestBody, containsString("as_user=false"))
    }
}