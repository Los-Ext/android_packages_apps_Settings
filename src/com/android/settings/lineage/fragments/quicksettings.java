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

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreferenceCompat;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.android.ThemeUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.lineage.support.preferences.SystemSettingListPreference;
import com.lineage.support.preferences.SystemSettingSeekBarPreference;

import java.util.List;
import java.util.ArrayList;

public class quicksettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "Lineage Customizations";

    private static final String KEY_QS_UI_STYLE  = "qs_tile_ui_style";

    private ListPreference mQsUI;

    private static ThemeUtils mThemeUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.lineage_quicksettings);
        final PreferenceScreen prefScreen = getPreferenceScreen();

        mThemeUtils = new ThemeUtils(getContext());

        final Context context = getContext();

        mQsUI = (ListPreference) findPreference(KEY_QS_UI_STYLE);
        mQsUI.setOnPreferenceChangeListener(this);

        checkQSOverlays(context);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final Context context = getContext();
        ContentResolver resolver = getContext().getContentResolver();
        if (preference == mQsUI) {
            int value = Integer.parseInt((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.QS_TILE_UI_STYLE, value, UserHandle.USER_CURRENT);
            updateQsStyle(getContext());
            checkQSOverlays(getContext());
            return true;
        }
        return false;
    }

    private static void updateQsStyle(Context context) {
        ContentResolver resolver = context.getContentResolver();

        boolean isA11Style = Settings.System.getIntForUser(resolver,
                Settings.System.QS_TILE_UI_STYLE , 0, UserHandle.USER_CURRENT) != 0;

        String qsUIStyleCategory = "android.theme.customization.qs_ui";
        String overlayThemeTarget  = "com.android.systemui";
        String overlayThemePackage  = "com.android.system.qs.ui.A11";

        if (mThemeUtils == null) {
            mThemeUtils = new ThemeUtils(context);
        }

        // reset all overlays before applying
        mThemeUtils.setOverlayEnabled(qsUIStyleCategory, overlayThemeTarget, overlayThemeTarget);

        if (isA11Style) {
            mThemeUtils.setOverlayEnabled(qsUIStyleCategory, overlayThemePackage, overlayThemeTarget);
        }
    }

    private void checkQSOverlays(Context context) {
        ContentResolver resolver = context.getContentResolver();
        int isA11Style = Settings.System.getIntForUser(resolver,
                Settings.System.QS_TILE_UI_STYLE , 0, UserHandle.USER_CURRENT);

        // Update summaries
        int index = mQsUI.findIndexOfValue(Integer.toString(isA11Style));
        mQsUI.setValue(Integer.toString(isA11Style));
        mQsUI.setSummary(mQsUI.getEntries()[index]);
    }

    @Override
    public int getMetricsCategory() {
       return MetricsProto.MetricsEvent.LINEAGE_SETTINGS;
    }
}
