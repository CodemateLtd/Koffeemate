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

package com.codemate.koffeemate.data.local

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CoffeePreferencesTest {
    lateinit var coffeePreferences: CoffeePreferences

    @Before
    fun setUp() {
        coffeePreferences = CoffeePreferences(InstrumentationRegistry.getTargetContext())
        coffeePreferences.preferences.edit().clear().apply()
    }

    @Test
    fun isCoffeeAnnouncementChannelSet_WhenNotSet_ReturnsFalse() {
        assertFalse(coffeePreferences.isCoffeeAnnouncementChannelSet())
    }

    @Test
    fun isCoffeeAnnouncementChannelSet_WhenIsSet_ReturnsTrue() {
        putString(coffeePreferences.announcementChannelKey, "test")
        assertTrue(coffeePreferences.isCoffeeAnnouncementChannelSet())
    }

    @Test
    fun isAccidentChannelSet_WhenNotSet_ReturnsFalse() {
        assertFalse(coffeePreferences.isAccidentChannelSet())
    }

    @Test
    fun isAccidentChannelSet_WhenIsSet_ButNotUsingSeparateChannels_ReturnsFalse() {
        putBoolean(coffeePreferences.differentChannelForAccidentsKey, false)
        putString(coffeePreferences.accidentChannelKey, "test")

        assertFalse(coffeePreferences.isAccidentChannelSet())
    }

    @Test
    fun isAccidentChannelSet_WhenIsSet_AndUsingSeparateChannels_ReturnsTrue() {
        putBoolean(coffeePreferences.differentChannelForAccidentsKey, true)
        putString(coffeePreferences.accidentChannelKey, "test")

        assertTrue(coffeePreferences.isAccidentChannelSet())
    }

    @Test
    fun isAccidentChannelSet_WhenAnnouncementChannelSet_AndNotUsingSeparateChannels_ReturnsTrue() {
        putBoolean(coffeePreferences.differentChannelForAccidentsKey, false)
        putString(coffeePreferences.announcementChannelKey, "test")

        assertTrue(coffeePreferences.isAccidentChannelSet())
    }

    private fun putString(key: String, value: String) {
        coffeePreferences.preferences.edit()
                .putString(key, value)
                .apply()
    }

    private fun putBoolean(key: String, value: Boolean) {
        coffeePreferences.preferences.edit()
                .putBoolean(key, value)
                .apply()
    }
}