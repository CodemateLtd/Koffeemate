package com.codemate.brewflop.ui.secretsettings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.MenuItem;
import android.widget.Toast;

import com.codemate.brewflop.R;

/**
 * Created by iiro on 4.10.2016.
 */
public class SecretSettingsActivity extends AppCompatActivity {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new SecretSettingsFragment())
                    .commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public static class SecretSettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.secret_preferences);

            Preference dayCount = findPreference(getString(R.string.preference_incident_free_days_key));
            dayCount.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String newValueStr = newValue.toString();
                    // TODO: dayCountUpdater.setCount(Integer.parseInt(newValueStr));

                    Toast.makeText(getActivity(), getString(R.string.preference_incident_free_days_updated_message, newValueStr), Toast.LENGTH_LONG).show();
                    return false;
                }
            });
        }
    }
}
