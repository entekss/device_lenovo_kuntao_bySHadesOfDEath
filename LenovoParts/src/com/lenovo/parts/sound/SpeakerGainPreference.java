/*
* Copyright (C) 2016 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package com.lenovo.parts.sound;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;

import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

import java.util.List;

import com.lenovo.parts.ProperSeekBarPreference;
import com.lenovo.parts.Utils;

public class SpeakerGainPreference extends ProperSeekBarPreference {

    private static int mMinVal = -10;
    private static int mMaxVal = 20;
    private static int mDefVal = 0;

    private static final String SPEAKER_GAIN_PATH = "/sys/kernel/sound_control/speaker_gain";

    public SpeakerGainPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInterval = 1;
        mShowSign = false;
        mUnits = "";
        mContinuousUpdates = false;
        mMinValue = mMinVal;
        mMaxValue = mMaxVal;
        mDefaultValueExists = true;
        mDefaultValue = mDefVal;
        mValue = Integer.parseInt(loadValue());

        setPersistent(false);
    }

    public static boolean isSupported() {
        return Utils.fileWritable(SPEAKER_GAIN_PATH);
    }

    public static void restore(Context context) {
        if (!isSupported()) {
            return;
        }

        String storedValue = PreferenceManager.getDefaultSharedPreferences(context).getString(SoundControlSettings.KEY_SPEAKER_GAIN, String.valueOf(mDefVal));
        Utils.writeValue(SPEAKER_GAIN_PATH, storedValue);
    }

    public static String loadValue() {
        int value = Integer.valueOf(Utils.getFileValue(SPEAKER_GAIN_PATH, String.valueOf(mDefVal)));
        if (value >= 0 && value <= 20) {
            return String.valueOf(value);
        } else if (value >= 246 && value <= 255) {
            return String.valueOf(value - 256);
        }
        return "";
    }

    private void saveValue(String newValue) {
        Utils.writeValue(SPEAKER_GAIN_PATH, newValue);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putString(SoundControlSettings.KEY_SPEAKER_GAIN, newValue);
        editor.apply();
    }

    @Override
    protected void changeValue(int newValue) {
        saveValue(String.valueOf(newValue));
    }
}

