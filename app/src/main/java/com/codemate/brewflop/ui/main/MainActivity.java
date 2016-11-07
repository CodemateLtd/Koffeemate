package com.codemate.brewflop.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.codemate.brewflop.BuildConfig;
import com.codemate.brewflop.DayCountUpdater;
import com.codemate.brewflop.R;
import com.codemate.brewflop.data.network.SlackMemeUploader;
import com.codemate.brewflop.data.network.SlackMessageCallback;
import com.codemate.brewflop.data.network.SlackService;
import com.codemate.brewflop.data.repository.FirebaseMemeRepository;
import com.codemate.brewflop.ui.secret.SecretSettingsActivity;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    static final String DEFAULT_PASSWORD_FOR_SETTINGS = "Google";
    private static final int GUILTY_NOOB_SPEECH_CODE = 69;

    private DayCountUpdater dayCountUpdater;
    private DayUpdateListener dayUpdateListener;
    private SlackMemeUploader memeUploader;

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

        memeUploader = new SlackMemeUploader(
                new FirebaseMemeRepository(FirebaseDatabase.getInstance()),
                new SlackService(BuildConfig.MEME_API_BASE_URL).getApi()
        );

        findViewById(R.id.resetButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForGuiltyCoffeeNoob();
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
                        confirmGuiltyCoffeeNoob(name);
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

    public void confirmGuiltyCoffeeNoob(final String name) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.reset_the_counter)
                .setMessage(getString(R.string.posting_to_slack_fmt, name))
                .setNegativeButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        askForGuiltyCoffeeNoob();
                    }
                })
                .setNeutralButton(R.string.cancel, null)
                .setPositiveButton(R.string.inform_everyone, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String message = getString(R.string.slack_announcement_fmt, name, dayCountUpdater.getDayCount());

                        dayCountUpdater.reset();
                        memeUploader.uploadRandomMeme(message, new SlackMessageCallback() {
                            @Override
                            public void onMessagePostedToSlack() {
                                Toast.makeText(MainActivity.this, R.string.message_posted_successfully, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onMessageError() {
                                Toast.makeText(MainActivity.this, R.string.could_not_post_message, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).show();
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
