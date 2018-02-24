package com.jtstegeman.cs4518_finalproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

public class SettingsActivity extends PreferenceActivity {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        findPreference(getString(R.string.pref_sms_blast_key)).setEnabled(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED);
    }
}
