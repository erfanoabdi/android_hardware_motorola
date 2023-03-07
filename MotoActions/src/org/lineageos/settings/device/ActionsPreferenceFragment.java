/*
 * Copyright (C) 2015-2016 The CyanogenMod Project
 * Copyright (C) 2017-2022 The LineageOS Project
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

package org.lineageos.settings.device;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;

import motorola.hardware.health.V2_0.IMotHealth;
import motorola.hardware.health.V2_0.Result;

public class ActionsPreferenceFragment extends PreferenceFragment implements
        Preference.OnPreferenceClickListener {

    private static final String KEY_ACTIONS_CATEGORY = "actions_key";
    public static final String KEY_ADAPTIVE_CHARGING = "adaptive_charging";

    protected Preference adaptiveCharging;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.actions_panel);

        adaptiveCharging = findPreference(KEY_ADAPTIVE_CHARGING);
        adaptiveCharging.setOnPreferenceClickListener(this);
        adaptiveCharging.setEnabled(false);

        try {
            IMotHealth adaptiveChargingService = IMotHealth.getService(false);
            if (adaptiveChargingService != null) {
                adaptiveChargingService.getChgLimits((result, lower, upper) -> {
                    if (result == Result.OK) {
                        adaptiveCharging.setEnabled(true);
                    }
                });
            }
        } catch (Exception e) { }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        final Context context = getActivity();
        if (context == null) {
            return false;
        }

        if (pref.getKey().equals(KEY_ADAPTIVE_CHARGING)) {
            AdaptiveChargingDialog.newInstance().show(getActivity().getFragmentManager(), KEY_ADAPTIVE_CHARGING);
            return true;
        }

        return false;
    }
}
