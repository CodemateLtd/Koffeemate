package com.codemate.brewflop.ui;

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
import android.widget.Toast;

import com.codemate.brewflop.DayCounter;
import com.codemate.brewflop.R;
import com.codemate.brewflop.network.SlackMessageCallback;
import com.codemate.brewflop.databinding.ActivityMainBinding;
import com.codemate.brewflop.network.SlackMemeUploader;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements MainView, SlackMessageCallback {
    private static final int GUILTY_NOOB_SPEECH_CODE = 69;

    private ActivityMainBinding binding;
    private LocalBroadcastManager broadcastManager;
    private DayCounter dayCounter;
    private MainPresenter presenter;

    private IntentFilter dayCountUpdatedFilter;
    private BroadcastReceiver dayCountUpdatedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        broadcastManager = LocalBroadcastManager.getInstance(this);
        dayCounter = new DayCounter(this);

        SlackMemeUploader memeUploader = SlackMemeUploader.getInstance();
        memeUploader.setCallback(this);

        presenter = new MainPresenter(this, dayCounter, memeUploader);

        binding.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.askForGuiltyCoffeeNoob();
            }
        });
        hideStatusBar();
    }

    @Override
    public void showGuiltyCoffeeNoobPrompt() {
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

    @Override
    public void updateDayCountText() {
        int dayCount = dayCounter.getDayCount();
        String formattedText = getResources().getQuantityString(R.plurals.number_of_days, dayCount, dayCount);

        binding.daysSinceLastIncident.setText(formattedText);
    }

    @Override
    public void onMessagePostedToSlack() {
        Toast.makeText(this, R.string.message_posted_successfully, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMessageError() {
        Toast.makeText(this, R.string.could_not_post_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void confirmGuiltyCoffeeNoob(final String name) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.reset_the_counter)
                .setMessage(getString(R.string.posting_to_slack_fmt, name))
                .setNegativeButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.askForGuiltyCoffeeNoob();
                    }
                })
                .setNeutralButton(R.string.cancel, null)
                .setPositiveButton(R.string.inform_everyone, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.resetCounterAndInformAboutANoob(name);
                    }
                }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerDayUpdateReceiver();
        updateDayCountText();
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

    private void registerDayUpdateReceiver() {
        if (dayCountUpdatedFilter == null || dayCountUpdatedReceiver == null) {
            dayCountUpdatedFilter = new IntentFilter(DayCounter.ACTION_DAY_COUNT_UPDATED);
            dayCountUpdatedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    updateDayCountText();
                }
            };
        }

        broadcastManager.registerReceiver(dayCountUpdatedReceiver, dayCountUpdatedFilter);
    }

    private void unregisterDayUpdateReceiver() {
        broadcastManager.unregisterReceiver(dayCountUpdatedReceiver);
    }
}
