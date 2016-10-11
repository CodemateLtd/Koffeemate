package com.codemate.brewflop;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.codemate.brewflop.ui.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
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
        String expectedDialogMessage = mainActivityTestRule.getActivity()
                .getString(R.string.posting_to_slack_fmt, "Jorma");
        intending(hasAction(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)).respondWith(createResultStub());

        onView(withId(R.id.resetButton)).perform(click());
        onView(withText(expectedDialogMessage)).check(matches(isDisplayed()));
    }

    private Instrumentation.ActivityResult createResultStub() {
        ArrayList<String> results = new ArrayList<>();
        results.add("Jorma");

        Intent resultData = new Intent();
        resultData.putExtra(RecognizerIntent.EXTRA_RESULTS, results);

        return new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
    }
}
