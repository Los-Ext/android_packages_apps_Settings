/*
 * Copyright (C) 2021 Wave-OS
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

package com.android.settings.lineage.fragments;

import android.os.Bundle;
import androidx.preference.*;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;

import com.android.settings.utils.TelephonyUtils;

import com.android.internal.logging.nano.MetricsProto;

public class notification extends SettingsPreferenceFragment {

    private static final String TAG = "Lineage Customizations";

    private static final String KEY_VIBRATE_CATEGORY = "incall_vib_options";
    private static final String KEY_VIBRATE_CONNECT = "vibrate_on_connect";
    private static final String KEY_VIBRATE_CALLWAITING = "vibrate_on_callwaiting";
    private static final String KEY_VIBRATE_DISCONNECT = "vibrate_on_disconnect";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.lineage_notification);

        final PreferenceScreen prefScreen = getPreferenceScreen();

        final PreferenceCategory vibCategory = prefScreen.findPreference(KEY_VIBRATE_CATEGORY);

        if (!TelephonyUtils.isVoiceCapable(getActivity())) {
            prefScreen.removePreference(vibCategory);
        }
    }

    @Override
    public int getMetricsCategory() {
       return MetricsProto.MetricsEvent.LINEAGE_SETTINGS;
    }
}
