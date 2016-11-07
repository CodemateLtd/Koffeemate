package com.codemate.brewflop.ui.main;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.codemate.brewflop.R;
import com.codemate.brewflop.ui.main.MainActivity;
import com.codemate.brewflop.ui.secret.SecretSettingsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by ironman on 11/10/16.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    @Rule
    public IntentsTestRule<MainActivity> mainActivityTestRule =
            new IntentsTestRule<>(MainActivity.class);

    @Test
    public void shouldConfirmPostingToSlackWithCorrectGuiltyPersonName() {
        String guiltyPersonName = "Jorma";
        String expectedDialogMessage = mainActivityTestRule.getActivity()
                .getString(R.string.posting_to_slack_fmt, guiltyPersonName);

        intending(hasAction(RecognizerIntent.ACTION_RECOGNIZE_SPEECH))
                .respondWith(createRecognizerResultWithName(guiltyPersonName));

        onView(withId(R.id.resetButton)).perform(click());
        onView(withText(expectedDialogMessage)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldLaunchSecretSettingsWhenCorrectPasswordSpoken() {
        String password = getPasswordForSettings(mainActivityTestRule.getActivity());

        intending(hasAction(RecognizerIntent.ACTION_RECOGNIZE_SPEECH))
                .respondWith(createRecognizerResultWithName(password));

        onView(withId(R.id.resetButton)).perform(click());
        intended(hasComponent(SecretSettingsActivity.class.getName()));
    }

    private String getPasswordForSettings(MainActivity mainActivity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        String passwordKey = mainActivity.getString(R.string.pref_key_secret_settings_password);
        String password = preferences.getString(passwordKey, MainActivity.DEFAULT_PASSWORD_FOR_SETTINGS);

        return password;
    }

    private Instrumentation.ActivityResult createRecognizerResultWithName(String name) {
        ArrayList<String> results = new ArrayList<>();
        results.add(name);

        Intent resultData = new Intent();
        resultData.putExtra(RecognizerIntent.EXTRA_RESULTS, results);

        return new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
    }
}
