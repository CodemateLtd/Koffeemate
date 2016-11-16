package com.codemate.brewflop.ui.main

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.longClick
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import com.codemate.brewflop.R
import com.codemate.brewflop.ui.secret.SecretSettingsActivity
import com.codemate.brewflop.ui.userselector.UserSelectorActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by ironman on 11/10/16.
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {
    @Rule
    @JvmField
    var mainActivityTestRule = IntentsTestRule(MainActivity::class.java)

    @Test
    fun shouldLaunchUserSelectorWhenResetButtonClicked() {
        onView(withId(R.id.resetButton)).perform(click())
        intended(hasComponent(UserSelectorActivity::class.java.name))
    }

    @Test
    fun shouldLaunchSecretSettingsWhenResetButtonLongClicked() {
        onView(withId(R.id.resetButton)).perform(longClick())
        intended(hasComponent(SecretSettingsActivity::class.java.name))
    }
}
