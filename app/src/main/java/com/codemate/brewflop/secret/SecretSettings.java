package com.codemate.brewflop.secret;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.codemate.brewflop.DayCounter;
import com.codemate.brewflop.R;

/**
 * Created by iiro on 4.10.2016.
 */
public class SecretSettings extends PreferenceActivity {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.secret_preferences);

        final DayCounter dayCounter = new DayCounter(this);

        Preference dayCount = findPreference(getString(R.string.pref_key_incident_free_day_count));
        dayCount.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String newValueStr = newValue.toString();
                dayCounter.setCount(Integer.parseInt(newValueStr));

                Toast.makeText(SecretSettings.this, getString(R.string.day_count_updated_fmt, newValueStr), Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }
}
