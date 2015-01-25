package ng.codehaven.eko.ui.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceScreen;

import com.github.machinarius.preferencefragment.PreferenceFragment;

import java.util.ArrayList;
import java.util.Collection;

import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.activities.SettingsActivity;

/**
 * Created by mrsmith on 11/14/14.
 * Settings Fragment
 */
public class SettingsPreferenceFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {


    private CheckBoxPreference decode1D;
    private CheckBoxPreference decodeQR;
    private CheckBoxPreference decodeDataMatrix;
    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        addPreferencesFromResource(R.xml.pref_general);
        PreferenceScreen preferences = getPreferenceScreen();
        preferences.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        decode1D = (CheckBoxPreference) preferences.findPreference(SettingsActivity.KEY_DECODE_1D);
        decodeQR = (CheckBoxPreference) preferences.findPreference(SettingsActivity.KEY_DECODE_QR);
        decodeDataMatrix = (CheckBoxPreference) preferences.findPreference(SettingsActivity.KEY_DECODE_DATA_MATRIX);
        disableLastCheckedPref();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        disableLastCheckedPref();
    }

    private void disableLastCheckedPref() {
        Collection<CheckBoxPreference> checked = new ArrayList<CheckBoxPreference>(3);
        if (decode1D.isChecked()) {
            checked.add(decode1D);
        }
        if (decodeQR.isChecked()) {
            checked.add(decodeQR);
        }
        if (decodeDataMatrix.isChecked()) {
            checked.add(decodeDataMatrix);
        }
        boolean disable = checked.size() < 2;
        CheckBoxPreference[] checkBoxPreferences = {decode1D, decodeQR, decodeDataMatrix};
        for (CheckBoxPreference pref : checkBoxPreferences) {
            pref.setEnabled(!(disable && checked.contains(pref)));
        }
    }
}
