package capstone.nanodegree.udacity.com.mypodcast.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.widget.Toast;

import capstone.nanodegree.udacity.com.mypodcast.R;

/**
 * Created by jem001 on 15/01/2018.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_settings);
        Preference preference = findPreference(getString(R.string.pref_top_podcast_key));
        Preference preference2 = findPreference(getString(R.string.pref_sync_time_key));
        preference.setOnPreferenceChangeListener(this);
        preference2.setOnPreferenceChangeListener(this);
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference p = preferenceScreen.getPreference(i);
            if (!(p instanceof CheckBoxPreference)) {
                if (p.getKey().equals(getString(R.string.pref_sync_time_key))) {
                    String value = sharedPreferences.getString(p.getKey(), getString(R.string.pref_sync_time_default));
                    setPreferenceSummary(p, value);

                } else if (p.getKey().equals(getString(R.string.pref_top_podcast_key))) {
                    String value = sharedPreferences.getString(p.getKey(), getString(R.string.pref_top_podcast_default));
                    setPreferenceSummary(p, value);
                }
            }
        }
    }

    private void setPreferenceSummary(Preference preference, String value) {
        if (preference instanceof EditTextPreference) {
            EditTextPreference editTextPreference = (EditTextPreference) preference;
            if (editTextPreference.getKey().equals(getString(R.string.pref_sync_time_key)))
                editTextPreference.setSummary(getString(R.string.every_day_sync_label) + value + getString(R.string.hour_label));
            else if (editTextPreference.getKey().equals(getString(R.string.pref_top_podcast_key)))
                editTextPreference.setSummary("Load top " + value + " podcast");
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (null != preference) {
            if (!(preference instanceof CheckBoxPreference)) {
                if (preference.getKey().equals(getString(R.string.pref_sync_time_key))) {
                    String value = sharedPreferences.getString(preference.getKey(), getString(R.string.pref_sync_time_default));
                    setPreferenceSummary(preference, value);

                } else if (preference.getKey().equals(getString(R.string.pref_top_podcast_key))) {
                    String value = sharedPreferences.getString(preference.getKey(), getString(R.string.pref_top_podcast_default));
                    setPreferenceSummary(preference, value);

                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Toast error = Toast.makeText(getContext(), R.string.error_preferences, Toast.LENGTH_SHORT);
        if (preference.getKey().equals(getString(R.string.pref_sync_time_key))) {
            String stringSize = ((String) (newValue)).trim();
            try {
                float size = Integer.parseInt(stringSize);
                return true;
            } catch (NumberFormatException nfe) {
                error.show();
                return false;
            }
        } else if (preference.getKey().equals(getString(R.string.pref_top_podcast_key))) {
            String stringSize = ((String) (newValue)).trim();
            try {
                float size = Integer.parseInt(stringSize);
                return true;
            } catch (NumberFormatException nfe) {
                error.show();
                return false;
            }
        }

        return true;
    }
}
