package com.codemate.brewflop.secret;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by iiro on 4.10.2016.
 */
public class SecretCodeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent secretPrefs = new Intent(context, SecretSettingsActivity.class);
        secretPrefs.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(secretPrefs);
    }
}
