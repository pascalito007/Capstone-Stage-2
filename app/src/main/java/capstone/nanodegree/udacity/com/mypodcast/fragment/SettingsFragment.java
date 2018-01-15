package capstone.nanodegree.udacity.com.mypodcast.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import capstone.nanodegree.udacity.com.mypodcast.R;

/**
 * Created by jem001 on 15/01/2018.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_settings);
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference p = preferenceScreen.getPreference(i);
            if (!(p instanceof CheckBoxPreference)) {
                if (p.getKey().equals("limit_podcast")) {
                    String value = sharedPreferences.getString(p.getKey(), "Sync data Every 24 hours");
                    setPreferenceSummary(p, value);

                } else if (p.getKey().equals("top_podcast_key")) {
                    String value = sharedPreferences.getString(p.getKey(), "Load top 50 podcast");
                    setPreferenceSummary(p, value);
                }
            }
        }
    }

    private void setPreferenceSummary(Preference preference, String value) {
        if (preference instanceof EditTextPreference) {
            EditTextPreference editTextPreference = (EditTextPreference) preference;
            if (editTextPreference.getKey().equals("limit_podcast"))
                editTextPreference.setSummary("Sync data Every " + value + " hours");
            else if (editTextPreference.getKey().equals("top_podcast_key"))
                editTextPreference.setSummary("Load top " + value + " podcast");
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (null != preference) {
            if (!(preference instanceof CheckBoxPreference)) {
                if (preference.getKey().equals("limit_podcast")) {
                    String value = sharedPreferences.getString(preference.getKey(), "Sync data Every 24 hours");
                    setPreferenceSummary(preference, value);

                } else if (preference.getKey().equals("top_podcast_key")) {
                    String value = sharedPreferences.getString(preference.getKey(), "Load top 50 podcast");
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
}
