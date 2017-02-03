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

package com.codemate.koffeemate.usecases

import com.codemate.koffeemate.BuildConfig
import com.codemate.koffeemate.data.network.SlackApi
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.core.IsEqual.equalTo
import org.hamcrest.core.StringContains.containsString
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import retrofit2.Response
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers

class SendCoffeeAnnouncementUseCaseTest {
    val CHANNEL_NAME = "fake-channel"

    lateinit var mockServer: MockWebServer
    lateinit var slackApi: SlackApi
    lateinit var useCase: SendCoffeeAnnouncementUseCase
    lateinit var testSubscriber: TestSubscriber<Response<ResponseBody>>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        mockServer = MockWebServer()
        mockServer.start()

        slackApi = SlackApi.create(mockServer.url("/"))
        useCase = SendCoffeeAnnouncementUseCase(
                slackApi,
                Schedulers.immediate(),
                Schedulers.immediate()
        )

        testSubscriber = TestSubscriber()
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun execute_MakesCorrectRequest() {
        mockServer.enqueue(MockResponse().setBody(""))
        useCase.execute(CHANNEL_NAME, "A happy message about coffee").subscribe(testSubscriber)

        val apiRequest = mockServer.takeRequest()
        assertThat(apiRequest.path, equalTo("/chat.postMessage"))

        val requestBody = apiRequest.body.readUtf8()
        assertThat(requestBody, containsString("token=${BuildConfig.SLACK_AUTH_TOKEN}"))
        assertThat(requestBody, containsString("channel=$CHANNEL_NAME"))
        assertThat(requestBody, containsString("text=A%20happy%20message%20about%20coffee"))
        assertThat(requestBody, containsString("as_user=false"))

        testSubscriber.assertCompleted()
        testSubscriber.assertNoErrors()
    }
}