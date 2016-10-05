package com.codemate.brewflop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.codemate.brewflop.databinding.ActivityMainBinding;
import com.codemate.brewflop.network.SlackMemeUploader;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int GUILTY_NOOB_SPEECH_CODE = 69;

    private ActivityMainBinding binding;
    private LocalBroadcastManager broadcastManager;
    private DayCounter dayCounter;

    private IntentFilter dayCountUpdatedFilter;
    private BroadcastReceiver dayCountUpdatedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        broadcastManager = LocalBroadcastManager.getInstance(this);
        dayCounter = new DayCounter(this);

        binding.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForGuiltyCoffeeNoob();
            }
        });
        hideStatusBar();
    }

    private void askForGuiltyCoffeeNoob() {
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
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String name = result.get(0);

                    confirmGuiltyCoffeeNoob(name);
                }

                break;
            }
        }
    }

    private void confirmGuiltyCoffeeNoob(final String name) {
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
                        resetCounterAndInformAboutANoob(name);
                    }
                }).show();
    }

    private void resetCounterAndInformAboutANoob(String name) {
        int incidentFreeDays = dayCounter.getDayCount();
        dayCounter.reset();

        SlackMemeUploader.getInstance().uploadRandomMeme(incidentFreeDays, name);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerDayUpdateReceiver();
        updateText();
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterDayUpdateReceiver();
    }

    private void hideStatusBar() {
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }

    private void updateText() {
        int dayCount = dayCounter.getDayCount();
        String formattedText = getResources().getQuantityString(R.plurals.number_of_days, dayCount, dayCount);

        binding.daysSinceLastIncident.setText(formattedText);
    }

    private void registerDayUpdateReceiver() {
        if (dayCountUpdatedFilter == null || dayCountUpdatedReceiver == null) {
            dayCountUpdatedFilter = new IntentFilter(DayCounter.ACTION_DAY_COUNT_UPDATED);
            dayCountUpdatedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    updateText();
                }
            };
        }

        broadcastManager.registerReceiver(dayCountUpdatedReceiver, dayCountUpdatedFilter);
    }

    private void unregisterDayUpdateReceiver() {
        broadcastManager.unregisterReceiver(dayCountUpdatedReceiver);
    }
}
