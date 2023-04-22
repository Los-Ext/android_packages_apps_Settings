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

import android.app.Activity;
import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;
import androidx.preference.*;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.crdroid.ThemeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.lineage.support.preferences.SystemSettingListPreference;
import com.lineage.support.preferences.SystemSettingSeekBarPreference;

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

        mThemeUtils = new ThemeUtils(getActivity());

        final Context context = getContext();

        mQsUI = (ListPreference) findPreference(KEY_QS_UI_STYLE);
        mQsUI.setOnPreferenceChangeListener(this);

        checkQSOverlays(context);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final Context context = getContext();
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mQsUI) {
            int value = Integer.parseInt((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.QS_TILE_UI_STYLE, value, UserHandle.USER_CURRENT);
            updateQsStyle(getActivity());
            checkQSOverlays(getActivity());
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

        if (isA11Style > 0) {
            mQsUI.setEnabled(true);
        } else {
            mQsUI.setEnabled(true);
        }

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
