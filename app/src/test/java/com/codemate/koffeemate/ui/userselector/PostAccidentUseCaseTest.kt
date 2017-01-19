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

package com.codemate.koffeemate.ui.userselector

import android.content.SharedPreferences
import android.graphics.Bitmap
import com.codemate.koffeemate.BuildConfig
import com.codemate.koffeemate.common.AwardBadgeCreator
import com.codemate.koffeemate.data.local.CoffeeEventRepository
import com.codemate.koffeemate.data.local.CoffeePreferences
import com.codemate.koffeemate.data.network.SlackApi
import com.codemate.koffeemate.data.network.SlackService
import com.codemate.koffeemate.data.network.models.Profile
import com.codemate.koffeemate.data.network.models.User
import com.codemate.koffeemate.testutils.RegexMatcher
import com.codemate.koffeemate.testutils.SynchronousExecutorService
import com.codemate.koffeemate.testutils.fakeUser
import com.codemate.koffeemate.testutils.getResourceFile
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import okhttp3.Dispatcher
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.core.StringContains
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import retrofit2.Response
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers

class PostAccidentUseCaseTest {
    val TEST_USER_ID = "abc123"

    @Mock
    lateinit var mockCoffeePreferences: CoffeePreferences

    @Mock
    lateinit var mockCoffeeEventRepository: CoffeeEventRepository

    @Mock
    lateinit var mockAwardBadgeCreator: AwardBadgeCreator

    @Mock
    lateinit var mockBitmap: Bitmap

    lateinit var mockServer: MockWebServer
    lateinit var slackApi: SlackApi
    lateinit var useCase: PostAccidentUseCase
    lateinit var testSubscriber: TestSubscriber<Response<ResponseBody>>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        mockServer = MockWebServer()
        mockServer.start()
        mockServer.enqueue(MockResponse().setBody(""))

        mockCoffeePreferences.preferences = mock<SharedPreferences>()
        whenever(mockCoffeePreferences.getAccidentChannel()).thenReturn("test-channel")
        whenever(mockCoffeeEventRepository.getAccidentCountForUser(TEST_USER_ID)).thenReturn(1)

        whenever(mockAwardBadgeCreator.createBitmapFileWithAward(mockBitmap, 1))
                .thenReturn(getResourceFile("images/empty.png"))

        slackApi = SlackService.getApi(Dispatcher(SynchronousExecutorService()), mockServer.url("/"))
        useCase = PostAccidentUseCase(
                slackApi,
                mockCoffeeEventRepository,
                mockCoffeePreferences,
                mockAwardBadgeCreator,
                Schedulers.immediate(),
                Schedulers.immediate()
        )

        testSubscriber = TestSubscriber<Response<ResponseBody>>()
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun announceCoffeeBrewingAccident_ShouldMakeCorrectRequest() {
        val user = fakeUser()
        useCase.execute("Test comment", user, mockBitmap).subscribe(testSubscriber)

        // TODO: There has to be a better way to verify these multipart post params, right? :S
        val requestBody = mockServer.takeRequest().body.readUtf8()
        assertThat(requestBody, StringContains.containsString("filename=\"jormas-certificate.png\""))
        assertThat(requestBody, RegexMatcher.matchesPattern(".*channels.*test-channel.*"))
        assertThat(requestBody, RegexMatcher.matchesPattern(".*initial_comment.*Test comment.*"))
        assertThat(requestBody, RegexMatcher.matchesPattern(".*token.*${BuildConfig.SLACK_AUTH_TOKEN}.*"))
    }

    @Test
    fun announceCoffeeBrewingAccident_WhenSuccessful_NotifiesUIAndStoresEvent() {
        val user = fakeUser()
        useCase.execute("", user, mockBitmap).subscribe(testSubscriber)

        testSubscriber.assertValueCount(1)
        testSubscriber.assertCompleted()

        verify(mockCoffeeEventRepository).recordBrewingAccident(user.id)
        verify(mockCoffeeEventRepository).getAccidentCountForUser(TEST_USER_ID)
        verifyNoMoreInteractions(mockCoffeeEventRepository)
    }
}