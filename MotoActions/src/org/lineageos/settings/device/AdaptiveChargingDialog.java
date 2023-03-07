/*
 * Copyright (c) 2015 The CyanogenMod Project
 * Copyright (c) 2017-2023 The LineageOS Project
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

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import android.app.DialogFragment;

import motorola.hardware.health.V2_0.IMotHealth;
import motorola.hardware.health.V2_0.Result;

public class AdaptiveChargingDialog extends DialogFragment implements SeekBar.OnSeekBarChangeListener {

    protected IMotHealth mAdaptiveChargingService;
    protected View mDialogView;
    public static AdaptiveChargingDialog newInstance() {
        return new AdaptiveChargingDialog();
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        try {
            mAdaptiveChargingService = IMotHealth.getService(false);
        } catch (Exception e) { }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mDialogView = View.inflate(getActivity(), R.layout.fragment_adaptive_charging_dialog, null);
        SeekBar lowerSeekBar = mDialogView.findViewById(R.id.lowerSeekBar);
        SeekBar upperSeekBar = mDialogView.findViewById(R.id.upperSeekBar);
        lowerSeekBar.setOnSeekBarChangeListener(this);
        upperSeekBar.setOnSeekBarChangeListener(this);
        try {
            mAdaptiveChargingService.getChgLimits((result, lower, upper) -> {
                if (result == Result.OK) {
                    lowerSeekBar.setProgress(lower);
                    upperSeekBar.setProgress(upper);
                }
            });
        } catch (Exception e) { }

        builder.setMessage(R.string.adaptive_charging_title)
                .setView(mDialogView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        int lower = ((SeekBar) mDialogView.findViewById(R.id.lowerSeekBar)).getProgress();
                        int upper = ((SeekBar) mDialogView.findViewById(R.id.upperSeekBar)).getProgress();
                        try {
                            mAdaptiveChargingService.setChgLimits(lower, upper);
                        } catch (Exception e) { }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.lowerSeekBar) {
            TextView lowerText = (TextView) mDialogView.findViewById(R.id.lowerSeekBarPrefValue);
            lowerText.setText(String.valueOf(progress) + "%");
        } else if (seekBar.getId() == R.id.upperSeekBar) {
            TextView upperText = (TextView) mDialogView.findViewById(R.id.upperSeekBarPrefValue);
            upperText.setText(String.valueOf(progress) + "%");
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

}
