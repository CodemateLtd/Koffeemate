package com.codemate.brewflop

import org.hamcrest.core.IsEqual.equalTo
import org.hamcrest.core.IsNot.not
import org.junit.Assert.assertThat
import org.junit.Test

class ConstantsTest {
    @Test
    fun shouldPickCorrectAnnouncementChannelBasedOnBuildFlavor() {
        if (BuildConfig.FLAVOR == "mock") {
            assertThat(Constants.ACCIDENT_ANNOUNCEMENT_CHANNEL, equalTo("iiro-test"))
        } else {
            assertThat(Constants.ACCIDENT_ANNOUNCEMENT_CHANNEL, not(equalTo("iiro-test")))
        }
    }
}