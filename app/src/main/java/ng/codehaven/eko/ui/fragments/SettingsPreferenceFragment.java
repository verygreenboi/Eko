package ng.codehaven.eko.ui.fragments;

import android.app.Activity;
import android.os.Bundle;

import com.github.machinarius.preferencefragment.PreferenceFragment;

import ng.codehaven.eko.R;

/**
 * Created by mrsmith on 11/14/14.
 * Settings Fragment
 */
public class SettingsPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        addPreferencesFromResource(R.xml.pref_general);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
