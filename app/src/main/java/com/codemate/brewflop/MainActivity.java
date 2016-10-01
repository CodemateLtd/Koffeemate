package com.codemate.brewflop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.codemate.brewflop.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private LocalBroadcastManager broadcastManager;
    private DayCounter dayCounter;

    private IntentFilter dayCountUpdatedFilter;
    private BroadcastReceiver dayCountUpdatedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        broadcastManager = LocalBroadcastManager.getInstance(this);
        dayCounter = new DayCounter(this, broadcastManager);

        binding.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dayCounter.reset();
            }
        });
        hideStatusBar();
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
