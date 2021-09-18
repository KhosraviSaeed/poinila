package com.shaya.poinila.android.presentation.view.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.shaya.poinila.android.presentation.R;

/**
 * Created by iran on 2015-11-07.
 */
public class AppSettingFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
