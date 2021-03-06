/*
 * Copyright (C) 2016 The CyanogenMod project
 * Copyright (C) 2017-2018 The LineageOS project
 * Copyright (C) 2019 The PixelExperience Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pixys.settings.fragments;

import android.content.Context;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.DropDownPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceGroup;
import androidx.preference.SwitchPreference;
import androidx.preference.ListPreference;
import android.view.View;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Traffic extends SettingsPreferenceFragment implements OnPreferenceChangeListener {
	private static final String TAG = "Traffic";

    private static final String NETWORK_TRAFFIC_CATEGORY = "network_traffic";

    private PreferenceCategory mNetworkTrafficCategory;
    private DropDownPreference mNetTrafficEnabled;
    private SwitchPreference mNetTrafficAutohide;
    private DropDownPreference mNetTrafficUnits;
    private SwitchPreference mNetTrafficShowUnits;

    private static List<String> sNonIndexableKeys = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.traffic);
        final ContentResolver resolver = getActivity().getContentResolver();

        mNetworkTrafficCategory = (PreferenceCategory) findPreference(NETWORK_TRAFFIC_CATEGORY);

        if (!isNetworkTrafficAvailable()) {
            getPreferenceScreen().removePreference(mNetworkTrafficCategory);
        }else{
            mNetTrafficEnabled = (DropDownPreference)
                    findPreference(Settings.System.NETWORK_TRAFFIC_ENABLED);
            mNetTrafficEnabled.setOnPreferenceChangeListener(this);
            int mode = Settings.System.getIntForUser(resolver,
                    Settings.System.NETWORK_TRAFFIC_ENABLED, 0, UserHandle.USER_CURRENT);
            mNetTrafficEnabled.setValue(String.valueOf(mode));

            mNetTrafficAutohide = (SwitchPreference)
                    findPreference(Settings.System.NETWORK_TRAFFIC_AUTOHIDE);
            mNetTrafficAutohide.setOnPreferenceChangeListener(this);

            mNetTrafficUnits = (DropDownPreference)
                    findPreference(Settings.System.NETWORK_TRAFFIC_UNITS);
            mNetTrafficUnits.setOnPreferenceChangeListener(this);
            int units = Settings.System.getIntForUser(resolver,
                    Settings.System.NETWORK_TRAFFIC_UNITS, /* Mbps */ 1, UserHandle.USER_CURRENT);
            mNetTrafficUnits.setValue(String.valueOf(units));

            mNetTrafficShowUnits = (SwitchPreference)
                    findPreference(Settings.System.NETWORK_TRAFFIC_SHOW_UNITS);
            mNetTrafficShowUnits.setOnPreferenceChangeListener(this);

            updateNetworkTrafficEnabledStates(mode);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Adjust status bar preferences for RTL
		        if (mNetworkTrafficCategory != null && !isNetworkTrafficAvailable()) {
            getPreferenceScreen().removePreference(mNetworkTrafficCategory);
        }
    }

    private boolean isNetworkTrafficAvailable(){
        if (getResources().getBoolean(
                com.android.internal.R.bool.config_physicalDisplayCutout)){
            return Settings.System.getIntForUser(getActivity().getContentResolver(),
                Settings.System.DISPLAY_CUTOUT_HIDDEN, 0, UserHandle.USER_CURRENT) == 1;
        }else{
            return true;
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNetTrafficEnabled) {
            int mode = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_ENABLED, mode);
            updateNetworkTrafficEnabledStates(mode);
        } else if (preference == mNetTrafficUnits) {
            int units = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_UNITS, units);
        }
        return true;
    }

    private void updateNetworkTrafficEnabledStates(int mode) {
        final boolean enabled = mode != 0;
        mNetTrafficAutohide.setEnabled(enabled);
        mNetTrafficUnits.setEnabled(enabled);
        mNetTrafficShowUnits.setEnabled(enabled);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.PIXYS;
    }
}
