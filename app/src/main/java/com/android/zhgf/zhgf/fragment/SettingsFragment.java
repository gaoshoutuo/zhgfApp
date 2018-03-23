package com.android.zhgf.zhgf.fragment;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.tool.SeekBarPreference;

/**
 * Created by TZXX001 on 2017/10/18.
 */

public class SettingsFragment extends PreferenceFragmentCompat {
/*    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }*/

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (!SeekBarPreference.onDisplayPreferenceDialog(this, preference)) {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
