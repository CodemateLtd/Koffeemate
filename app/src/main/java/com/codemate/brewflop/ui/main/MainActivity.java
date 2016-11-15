package com.codemate.brewflop.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.codemate.brewflop.DayCountUpdater;
import com.codemate.brewflop.R;
import com.codemate.brewflop.ui.secret.SecretSettingsActivity;
import com.codemate.brewflop.ui.userselector.UserSelectorActivity;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    static final String DEFAULT_PASSWORD_FOR_SETTINGS = "Settings";
    private static final int GUILTY_NOOB_SPEECH_CODE = 69;

    private DayCountUpdater dayCountUpdater;
    private DayUpdateListener dayUpdateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        dayCountUpdater = new DayCountUpdater(this);
        dayUpdateListener = new DayUpdateListener(
                LocalBroadcastManager.getInstance(this),
                new DayUpdateListener.OnDayChangedListener() {
                    @Override
                    public void onDayChanged() {
                        updateDayCountText();
                    }
                }
        );

        findViewById(R.id.resetButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserSelectorActivity.class);
                startActivity(intent);
                // askForGuiltyCoffeeNoob();
            }
        });

        hideStatusBar();
    }

    public void askForGuiltyCoffeeNoob() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.who_is_guilty));

        startActivityForResult(intent, GUILTY_NOOB_SPEECH_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GUILTY_NOOB_SPEECH_CODE: {
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> results = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    if (containsPasswordForSettings(results)) {
                        Intent intent = new Intent(MainActivity.this, SecretSettingsActivity.class);
                        startActivity(intent);
                    } else {
                        String name = results.get(0);
                        Intent intent = new Intent(this, UserSelectorActivity.class);
                        intent.putExtra("search_term", name);
                        startActivity(intent);
                    }
                }

                break;
            }
        }
    }

    private boolean containsPasswordForSettings(ArrayList<String> results) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String secret = preferences.getString(
                getString(R.string.pref_key_secret_settings_password),
                DEFAULT_PASSWORD_FOR_SETTINGS
        );

        for (String result : results) {
            if (secret.toLowerCase().equals(result.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public void updateDayCountText() {
        int dayCount = dayCountUpdater.getDayCount();
        String formattedText = getResources().getQuantityString(R.plurals.number_of_days, dayCount, dayCount);

        ((TextView) findViewById(R.id.daysSinceLastIncident)).setText(formattedText);
    }

    @Override
    protected void onResume() {
        super.onResume();

        dayUpdateListener.listenForDayChanges();
        updateDayCountText();
    }

    @Override
    protected void onStop() {
        super.onStop();

        dayUpdateListener.stopListeningForDayChanges();
    }

    private void hideStatusBar() {
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }
}
